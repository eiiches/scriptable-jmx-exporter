package net.thisptr.jmx.exporter.agent.scraper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

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

	private final MBeanServer server;

	private final List<ScrapeRuleType> rules;
	private final ScrapeRuleType defaultRule;

	/**
	 * Caches an MBeanInfo for an MBean.
	 *
	 * <p>
	 * MBeanInfo should only be cached for a limited amount of time, because there's a dynamic MBean
	 * which can change its interface dynamically at runtime. Section 2.3.2.2 of JMX Specification 1.4
	 * explicitly states that an MBeanInfo of a dynamic MBean is allowed to vary, while noting implications
	 * of this behavior.
	 * </p>
	 *
	 * @see https://docs.oracle.com/javase/8/docs/technotes/guides/jmx/JMX_1_4_specification.pdf
	 */
	private final LoadingCache<ObjectName, CachedMBeanInfo> mbeanInfoCache = CacheBuilder.newBuilder()
			.refreshAfterWrite(60, TimeUnit.SECONDS)
			.build(new CacheLoader<ObjectName, CachedMBeanInfo>() {
				@Override
				public CachedMBeanInfo load(final ObjectName name) {
					return prepare(name);
				}
			});

	/**
	 * Caches a scrape rule for an MBean. While the mappings never change, we need to drop stale entries to
	 * avoid consuming too much memory.
	 */
	private final LoadingCache<FastObjectName, Pair<Boolean, ScrapeRuleType>> findRuleEarlyCache = CacheBuilder.newBuilder()
			.expireAfterWrite(600, TimeUnit.SECONDS)
			.build(new CacheLoader<FastObjectName, Pair<Boolean, ScrapeRuleType>>() {
				@Override
				public Pair<Boolean, ScrapeRuleType> load(final FastObjectName name) {
					return findRuleEarlyNoCache(name);
				}
			});

	/**
	 * Caches a scrape rule for an MBean attribute. While the mappings never change, we need to drop stale entries to
	 * avoid consuming too much memory.
	 */
	private final LoadingCache<Pair<FastObjectName, String>, ScrapeRuleType> findRuleCache = CacheBuilder.newBuilder()
			.expireAfterWrite(600, TimeUnit.SECONDS)
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

	public Scraper(final MBeanServer server, final List<ScrapeRuleType> rules, ScrapeRuleType defaultRule) {
		this.server = server;
		this.rules = rules;
		this.defaultRule = defaultRule;
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
		return Pair.of(true, defaultRule); // default rule should be used
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
		return defaultRule;
	}

	private class AttributeRule {
		public final MBeanAttributeInfo attribute;
		public final ScrapeRuleType rule;

		public AttributeRule(final MBeanAttributeInfo attribute, final ScrapeRuleType rule) {
			this.attribute = attribute;
			this.rule = rule;
		}
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output) throws InterruptedException {
		scrape(output, 0L, TimeUnit.MILLISECONDS);
	}

	public final CachedMBeanInfo NULL_CACHED_MBEAN_INFO = new CachedMBeanInfo(null, null, null, null);

	private class CachedMBeanInfo {
		public final FastObjectName name;
		public final MBeanInfo info;

		public final Map<String, AttributeRule> requests;
		public final String[] attributeNamesToGet;

		public CachedMBeanInfo(final FastObjectName name, final MBeanInfo info, final Map<String, AttributeRule> requests, final String[] attributeNamesToRequest) {
			this.name = name;
			this.info = info;
			this.attributeNamesToGet = attributeNamesToRequest;
			this.requests = requests;
		}
	}

	private CachedMBeanInfo prepare(final ObjectName _name) {
		final FastObjectName name = new FastObjectName(_name);

		// Filter early by ObjectName because server.getMBeanInfo() is really slow.
		final Pair<Boolean, ScrapeRuleType> ruleByName = findRuleEarly(name);
		if (ruleByName._1) { // If we were able to determine rule solely by ObjectName
			if (ruleByName._2 != null && ruleByName._2.skip()) // and if the rule is to skip MBean
				return NULL_CACHED_MBEAN_INFO;
		}

		final MBeanInfo info;
		try {
			info = server.getMBeanInfo(_name);
		} catch (final Throwable th) {
			LOG.log(Level.FINER, "Failed to obtain MBeanInfo (name = " + name + ")", th);
			return NULL_CACHED_MBEAN_INFO;
		}

		final Set<String> bannedAttributes = bannedMBeanAttributes.getIfPresent(_name);

		final Map<String, AttributeRule> requests = new HashMap<>();
		final List<String> attributes = new ArrayList<>();
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

				requests.put(attribute.getName(), new AttributeRule(attribute, rule));
				attributes.add(attribute.getName());
			} catch (final Throwable th) {
				LOG.log(Level.WARNING, "Failed to process MBean attribute (name = " + name + ", attribute = " + attribute.getName() + ").", th);
			}
		}

		return new CachedMBeanInfo(name, info, requests, attributes.toArray(new String[0]));
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output, final long duration, final TimeUnit unit) throws InterruptedException {
		// We use server.queryNames() here, instead of server.queryMBeans(), to avoid costly server.getMBeanInfo() invoked internally.
		final Set<ObjectName> names;
		try {
			names = server.queryNames(null, null);
		} catch (final Throwable th) {
			LOG.log(Level.WARNING, "Failed to enumerate MBean names.", th);
			return;
		}

		MoreCollections.forEachSlowlyOverDuration(names, duration, unit, (_name) -> {
			final CachedMBeanInfo info = mbeanInfoCache.getUnchecked(_name);
			if (info == NULL_CACHED_MBEAN_INFO)
				return;

			final long timestamp;
			final AttributeList obtainedAttributes;
			try {
				timestamp = System.currentTimeMillis();
				// NOTE: We assume the results are not always in the same order as the arguments as the javadoc
				// doesn't say anything about it.
				obtainedAttributes = server.getAttributes(_name, info.attributeNamesToGet);
			} catch (final Throwable e) {
				LOG.log(Level.WARNING, "Failed to process MBean (name = " + _name + ")", e);
				return;
			}

			if (obtainedAttributes.size() == info.attributeNamesToGet.length) { // all retrieved
				for (final Attribute attribute : obtainedAttributes.asList()) {
					final AttributeRule request = info.requests.get(attribute.getName());
					output.emit(new Sample<>(request.rule, timestamp, info.name, info.info, request.attribute, attribute.getValue()));
				}
			} else { // some of the attributes could not be retrieved; we need to check which attributes are missing
				final Map<String, AttributeRule> requests = new HashMap<>(info.requests);
				final String[] successfulAttributeNames = new String[obtainedAttributes.size()];
				int i = 0;
				for (final Attribute attribute : obtainedAttributes.asList()) {
					final AttributeRule request = requests.remove(attribute.getName());
					output.emit(new Sample<>(request.rule, timestamp, info.name, info.info, request.attribute, attribute.getValue()));
					successfulAttributeNames[i++] = attribute.getName();
				}
				requests.forEach((attributeName, request) -> {
					try {
						server.getAttribute(_name, attributeName);
					} catch (final Throwable th) {
						if (th.getCause() instanceof UnsupportedOperationException) {
							// Disable attribute for 10 minutes, iff unsupported.
							banAttribute(_name, attributeName);
						}
						LOG.log(Level.FINER, "Failed to obtain MBean attribute (name = " + _name + ", attribute = " + attributeName + ").", th);
					}
				});
				// Anyway, exclude failed attribute for 1 minutes.
				mbeanInfoCache.put(_name, new CachedMBeanInfo(info.name, info.info, info.requests, successfulAttributeNames));
			}
		});
	}

	/**
	 * Temporarily bans an attribute known to cause errors while scraping.
	 *
	 * @param name
	 * @param info
	 * @param attribute
	 * @return
	 */
	private boolean banAttribute(final ObjectName name, final String attribute) {
		return bannedMBeanAttributes.getUnchecked(name).add(attribute);
	}
}
