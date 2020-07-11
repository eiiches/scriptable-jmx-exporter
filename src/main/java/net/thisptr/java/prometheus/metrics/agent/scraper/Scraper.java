package net.thisptr.java.prometheus.metrics.agent.scraper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.thisptr.jackson.jq.internal.misc.Pair;
import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.misc.AttributeNamePattern;
import net.thisptr.java.prometheus.metrics.agent.utils.MoreCollections;

public class Scraper<ScrapeRuleType extends ScrapeRule> {
	private static final Logger LOG = Logger.getLogger(Scraper.class.getName());

	private final List<ScrapeRuleType> rules;
	private final MBeanServer server;

	private final LoadingCache<ObjectName, MBeanInfo> mbeanInfoCache = CacheBuilder.newBuilder()
			.refreshAfterWrite(60, TimeUnit.SECONDS)
			.build(new CacheLoader<ObjectName, MBeanInfo>() {
				@Override
				public MBeanInfo load(final ObjectName name) throws Exception {
					return server.getMBeanInfo(name);
				}
			});

	private final LoadingCache<ObjectName, Set<String>> bannedMBeanAttributes = CacheBuilder.newBuilder()
			.expireAfterWrite(600, TimeUnit.SECONDS)
			.build(new CacheLoader<ObjectName, Set<String>>() {
				@Override
				public Set<String> load(final ObjectName name) {
					return Collections.newSetFromMap(new ConcurrentHashMap<>());
				}
			});

	public Scraper(final MBeanServer server, final List<ScrapeRuleType> rules) {
		this.server = server;
		this.rules = rules;
	}

	private Pair<Boolean, ScrapeRuleType> findRuleEarly(final ObjectName name) {
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty())
				return Pair.of(true, rule); // found
			boolean nameMatches = false;
			for (final AttributeNamePattern pattern : rule.patterns()) {
				if (pattern.nameMatches(name)) {
					nameMatches = true;
					if (pattern.attribute == null)
						return Pair.of(true, rule); // found
				}
			}
			if (nameMatches)
				return Pair.of(false, null); // match depends on attribute, abort
		}
		return Pair.of(true, null); // default rule should be used
	}

	private ScrapeRuleType findRule(final ObjectName name, final String attribute) {
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty())
				return rule;
			for (final AttributeNamePattern pattern : rule.patterns())
				if (pattern.matches(name, attribute))
					return rule;
		}
		return null;
	}

	private class AttributeScrapeRequest {
		public final ObjectName name;
		public final MBeanInfo info;
		public final MBeanAttributeInfo attribute;
		public final ScrapeRuleType rule;

		public AttributeScrapeRequest(final ObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final ScrapeRuleType rule) {
			this.name = name;
			this.info = info;
			this.attribute = attribute;
			this.rule = rule;
		}
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output) throws InterruptedException {
		scrape(output, 0L, TimeUnit.MILLISECONDS);
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output, final long duration, final TimeUnit unit) throws InterruptedException {
		final List<AttributeScrapeRequest> requests = new ArrayList<>();

		// We use server.queryNames() here, instead of server.queryMBeans(), to avoid costly server.getMBeanInfo() invoked internally.
		final Set<ObjectName> names;
		try {
			names = server.queryNames(null, null);
		} catch (final Throwable th) {
			LOG.log(Level.WARNING, "Failed to enumerate MBean names.", th);
			return;
		}

		for (final ObjectName name : names) {
			// Filter early by ObjectName because server.getMBeanInfo() is really slow.
			final Pair<Boolean, ScrapeRuleType> ruleByName = findRuleEarly(name);
			if (ruleByName._1) { // If we were able to determine rule solely by ObjectName
				if (ruleByName._2 != null && ruleByName._2.skip()) // and if the rule is to skip MBean
					continue;
			}

			final MBeanInfo info;
			try {
				info = mbeanInfoCache.get(name);
			} catch (final Throwable th) {
				LOG.log(Level.FINER, "Failed to obtain MBeanInfo (name = " + name + ")", th);
				continue;
			}

			final Set<String> bannedAttributes = bannedMBeanAttributes.getIfPresent(name);

			for (final MBeanAttributeInfo attribute : info.getAttributes()) {
				try {
					if (!attribute.isReadable())
						continue;

					if (bannedAttributes != null && bannedAttributes.contains(attribute.getName()))
						continue;

					final ScrapeRuleType rule;
					if (ruleByName._1) {
						rule = ruleByName._2;
					} else {
						rule = findRule(name, attribute.getName());
						if (rule != null && rule.skip())
							continue;
					}

					requests.add(new AttributeScrapeRequest(name, info, attribute, rule));
				} catch (final Throwable th) {
					LOG.log(Level.WARNING, "Failed to process MBean attribute (name = " + name + ", attribute = " + attribute.getName() + ").", th);
				}
			}
		}

		MoreCollections.forEachSlowlyOverDuration(requests, duration, unit, (request) -> {
			try {
				scrape(request.name, request.info, request.attribute, request.rule, output);
			} catch (final Throwable th) {
				LOG.log(Level.FINER, "Failed to scrape the attribute of the MBean instance (name = " + request.name + ", attribute = " + request.attribute.getName() + ")", th);
			}
		});
	}

	private void scrape(final ObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final ScrapeRuleType rule, final ScrapeOutput<ScrapeRuleType> output) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
		final long timestamp = System.currentTimeMillis();
		final Object value;
		try {
			value = server.getAttribute(name, attribute.getName());
		} catch (final RuntimeMBeanException e) {
			if (e.getCause() instanceof UnsupportedOperationException) {
				// ban attributes temporarily
				banAttribute(name, info, attribute);
				return;
			}
			throw e;
		}

		output.emit(new Sample<>(rule, timestamp, name, info, attribute, value));
	}

	/**
	 * Temporarily bans an attribute known to cause errors while scraping.
	 *
	 * @param name
	 * @param info
	 * @param attribute
	 */
	private void banAttribute(final ObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute) {
		bannedMBeanAttributes.getUnchecked(name).add(attribute.getName());
	}
}
