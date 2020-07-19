package net.thisptr.jmx.exporter.agent.scraper;

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

import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;
import net.thisptr.jmx.exporter.agent.misc.FastObjectName;
import net.thisptr.jmx.exporter.agent.misc.Pair;
import net.thisptr.jmx.exporter.agent.utils.MoreCollections;

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

	private final LoadingCache<ObjectName, FastObjectName> objectNameCache = CacheBuilder.newBuilder()
			.refreshAfterWrite(600, TimeUnit.SECONDS)
			.build(new CacheLoader<ObjectName, FastObjectName>() {
				@Override
				public FastObjectName load(final ObjectName name) {
					return new FastObjectName(name);
				}
			});

	private final LoadingCache<FastObjectName, Pair<Boolean, ScrapeRuleType>> findRuleEarlyCache = CacheBuilder.newBuilder()
			.refreshAfterWrite(600, TimeUnit.SECONDS)
			.build(new CacheLoader<FastObjectName, Pair<Boolean, ScrapeRuleType>>() {
				@Override
				public Pair<Boolean, ScrapeRuleType> load(final FastObjectName name) {
					return findRuleEarlyNoCache(name);
				}
			});

	private final LoadingCache<Pair<FastObjectName, String>, ScrapeRuleType> findRuleCache = CacheBuilder.newBuilder()
			.refreshAfterWrite(600, TimeUnit.SECONDS)
			.build(new CacheLoader<Pair<FastObjectName, String>, ScrapeRuleType>() {
				@Override
				public ScrapeRuleType load(final Pair<FastObjectName, String> args) throws Exception {
					return findRuleNoCache(args._1, args._2);
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

	private Pair<Boolean, ScrapeRuleType> findRuleEarly(final FastObjectName name) {
		return findRuleEarlyCache.getUnchecked(name);
	}

	private Pair<Boolean, ScrapeRuleType> findRuleEarlyNoCache(final FastObjectName name) {
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty())
				return Pair.of(true, rule); // found
			boolean nameMatches = false;
			for (final AttributeNamePattern pattern : rule.patterns()) {
				if (pattern.nameMatches(name.domain(), name.keyProperties())) {
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

	private ScrapeRuleType findRule(final FastObjectName name, final String attribute) {
		return findRuleCache.getUnchecked(Pair.of(name, attribute));
	}

	private ScrapeRuleType findRuleNoCache(final FastObjectName name, final String attribute) {
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty())
				return rule;
			for (final AttributeNamePattern pattern : rule.patterns())
				if (pattern.matches(name.domain(), name.keyProperties(), attribute))
					return rule;
		}
		return null;
	}

	private class AttributeScrapeRequest {
		public final FastObjectName name;
		public final MBeanInfo info;
		public final MBeanAttributeInfo attribute;
		public final ScrapeRuleType rule;

		public AttributeScrapeRequest(final FastObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final ScrapeRuleType rule) {
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

		for (final ObjectName _name : names) {
			final FastObjectName name = objectNameCache.getUnchecked(_name);

			// Filter early by ObjectName because server.getMBeanInfo() is really slow.
			final Pair<Boolean, ScrapeRuleType> ruleByName = findRuleEarly(name);
			if (ruleByName._1) { // If we were able to determine rule solely by ObjectName
				if (ruleByName._2 != null && ruleByName._2.skip()) // and if the rule is to skip MBean
					continue;
			}

			final MBeanInfo info;
			try {
				info = mbeanInfoCache.get(name.objectName());
			} catch (final Throwable th) {
				LOG.log(Level.FINER, "Failed to obtain MBeanInfo (name = " + name + ")", th);
				continue;
			}

			final Set<String> bannedAttributes = bannedMBeanAttributes.getIfPresent(name.objectName());

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

	private void scrape(final FastObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final ScrapeRuleType rule, final ScrapeOutput<ScrapeRuleType> output) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
		final long timestamp = System.currentTimeMillis();
		final Object value;
		try {
			value = server.getAttribute(name.objectName(), attribute.getName());
		} catch (final RuntimeMBeanException e) {
			if (e.getCause() instanceof UnsupportedOperationException) {
				// ban attributes temporarily
				banAttribute(name.objectName(), info, attribute);
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
