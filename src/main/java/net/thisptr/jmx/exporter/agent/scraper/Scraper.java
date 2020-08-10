package net.thisptr.jmx.exporter.agent.scraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
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
				public CachedMBeanInfo load(final ObjectName name) throws InstanceNotFoundException {
					return prepare(name);
				}
			});

	/**
	 * Caches a scrape rule for an MBean. While the mappings never change, we need to drop stale entries to
	 * avoid consuming too much memory.
	 */
	private final LoadingCache<FastObjectName, Pair<Boolean, RuleMatch>> findRuleEarlyCache = CacheBuilder.newBuilder()
			.expireAfterWrite(600, TimeUnit.SECONDS)
			.build(new CacheLoader<FastObjectName, Pair<Boolean, RuleMatch>>() {
				@Override
				public Pair<Boolean, RuleMatch> load(final FastObjectName name) {
					return findRuleEarlyNoCache(name);
				}
			});

	/**
	 * Caches a scrape rule for an MBean attribute. While the mappings never change, we need to drop stale entries to
	 * avoid consuming too much memory.
	 */
	private final LoadingCache<AttributeRuleCacheKey, RuleMatch> findRuleCache = CacheBuilder.newBuilder()
			.expireAfterWrite(600, TimeUnit.SECONDS)
			.build(new CacheLoader<AttributeRuleCacheKey, RuleMatch>() {
				@Override
				public RuleMatch load(final AttributeRuleCacheKey key) throws Exception {
					return findRuleNoCache(key);
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

	private Pair<Boolean, RuleMatch> findRuleEarly(final FastObjectName name) {
		return findRuleEarlyCache.getUnchecked(name);
	}

	private Pair<Boolean, RuleMatch> findRuleEarlyNoCache(final FastObjectName name) {
		final Map<String, String> captures = new LinkedHashMap<>(); // LinkedHashMap is used for faster iterations.
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty()) {
				if (rule.condition() == null) {
					return Pair.of(true, new RuleMatch(rule, Collections.emptyMap())); // found
				} else {
					return Pair.of(false, null); // match depends on condition, abort
				}
			}
			boolean nameMatches = false;
			for (final AttributeNamePattern pattern : rule.patterns()) {
				if (pattern.nameMatches(name.domain(), name.keyProperties(), captures)) {
					nameMatches = true;
					if (pattern.attribute == null && rule.condition() == null)
						return Pair.of(true, new RuleMatch(rule, Collections.unmodifiableMap(captures))); // found
				}
				captures.clear();
			}
			if (nameMatches)
				return Pair.of(false, null); // match depends on attribute or condition, abort
		}
		return Pair.of(true, new RuleMatch(defaultRule, Collections.emptyMap())); // default rule should be used
	}

	private static class AttributeRuleCacheKey {
		public final FastObjectName name;
		public final String attributeName;
		public final MBeanInfo beanInfo;
		public final MBeanAttributeInfo attributeInfo;

		public AttributeRuleCacheKey(final FastObjectName name, final String attributeName, final MBeanInfo beanInfo, final MBeanAttributeInfo attributeInfo) {
			this.name = name;
			this.attributeName = attributeName;
			this.beanInfo = beanInfo;
			this.attributeInfo = attributeInfo;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AttributeRuleCacheKey other = (AttributeRuleCacheKey) obj;
			if (attributeName == null) {
				if (other.attributeName != null)
					return false;
			} else if (!attributeName.equals(other.attributeName))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}

	private RuleMatch findRule(final FastObjectName name, final String attributeName, final MBeanInfo beanInfo, final MBeanAttributeInfo attributeInfo) {
		return findRuleCache.getUnchecked(new AttributeRuleCacheKey(name, attributeName, beanInfo, attributeInfo));
	}

	private RuleMatch findRuleNoCache(final AttributeRuleCacheKey key) {
		final Map<String, String> captures = new HashMap<>();
		for (final ScrapeRuleType rule : rules) {
			boolean patternMatches = false;
			if (rule.patterns() == null || rule.patterns().isEmpty()) {
				patternMatches = true;
			} else {
				for (final AttributeNamePattern pattern : rule.patterns()) {
					if (pattern.matches(key.name.domain(), key.name.keyProperties(), key.attributeName, captures)) {
						patternMatches = true;
						break;
					}
					captures.clear(); // clear partially matched captures
				}
			}
			if (!patternMatches)
				continue;
			if (rule.condition() == null || rule.condition().evaluate(key.beanInfo, key.attributeInfo))
				return new RuleMatch(rule, Collections.unmodifiableMap(captures));
		}
		return new RuleMatch(defaultRule, Collections.emptyMap());
	}

	private class AttributeRule {
		public final MBeanAttributeInfo attribute;
		public final RuleMatch ruleMatch;

		public AttributeRule(final MBeanAttributeInfo attribute, final RuleMatch ruleMatch) {
			this.attribute = attribute;
			this.ruleMatch = ruleMatch;
		}
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output) throws InterruptedException {
		scrape(output, 0L, TimeUnit.MILLISECONDS);
	}

	/**
	 * Null object to use with Guava cache, because Guava cache cannot cache null values.
	 */
	public final CachedMBeanInfo MBEAN_INFO_NEGATIVE_CACHE = new CachedMBeanInfo(null, null, null, null);

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

	private class RuleMatch {
		public final ScrapeRuleType rule;
		public final Map<String, String> captures;

		public RuleMatch(final ScrapeRuleType rule, final Map<String, String> captures) {
			this.rule = rule;
			this.captures = captures;
		}
	}

	private CachedMBeanInfo prepare(final ObjectName _name) throws InstanceNotFoundException {
		final FastObjectName name = new FastObjectName(_name);

		// Filter early by ObjectName because server.getMBeanInfo() is really slow.
		final Pair<Boolean, RuleMatch> ruleByName = findRuleEarly(name);
		if (ruleByName._1) { // If we were able to determine rule solely by ObjectName
			if (ruleByName._2.rule.skip()) // and if the rule is to skip MBean
				return MBEAN_INFO_NEGATIVE_CACHE;
		}

		final MBeanInfo info;
		try {
			info = server.getMBeanInfo(_name);
		} catch (final InstanceNotFoundException e) {
			throw e;
		} catch (final Throwable th) {
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE, String.format("MBeanServer#getMBeanInfo(%s) #=> %s. This indicates a bug in the MBean. The MBean will be banned for 1 minutes.", _name, th.getClass().getSimpleName()), th);
			return MBEAN_INFO_NEGATIVE_CACHE;
		}
		if (info == null) {
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE, String.format("MBeanServer#getMBeanInfo(%s) #=> null. This indicates a bug in the MBean. The MBean will be banned for 1 minutes.", _name));
			return MBEAN_INFO_NEGATIVE_CACHE;
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

				final RuleMatch ruleMatch;
				if (ruleByName._1) {
					ruleMatch = ruleByName._2;
				} else {
					ruleMatch = findRule(name, attribute.getName(), info, attribute);
					if (ruleMatch.rule.skip())
						continue;
				}

				requests.put(attribute.getName(), new AttributeRule(attribute, ruleMatch));
				attributes.add(attribute.getName());
			} catch (final Throwable th) {
				if (LOG.isLoggable(Level.WARNING))
					LOG.log(Level.WARNING, String.format("Got an unexpected exception while enumerating the MBean attribute \"%s:%s\". This is likely a bug in scriptable-jmx-exporter. Please report an issue on GitHub.", name, attribute.getName()), th);
				continue;
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
			if (LOG.isLoggable(Level.SEVERE))
				LOG.log(Level.SEVERE, String.format("MBeanServer#queryNames(null, null) #=> %s. This is bad. We can't do anything but to return an empty response.", th.getClass().getSimpleName()), th);
			return;
		}
		if (names == null) {
			LOG.log(Level.SEVERE, "MBeanServer#queryNames(null, null) #=> null. This is bad. We can't do anything but to return an empty response.");
			return;
		}

		MoreCollections.forEachSlowlyOverDuration(names, duration, unit, (_name) -> {
			try {
				scrape(output, _name);
			} catch (final InstanceNotFoundException e) {
				if (LOG.isLoggable(Level.FINE))
					LOG.log(Level.FINE, String.format("MBeanServer#getMBeanInfo(%s) #=> %s. This can happen when the MBean is unregistered after queryNames() and is just a temporary thing.", _name, e.getClass().getSimpleName()), e);
			} catch (final Throwable th) {
				if (LOG.isLoggable(Level.WARNING))
					LOG.log(Level.WARNING, String.format("Got an unexpected exception while processing the MBean \"%s\". This is likely a bug in scriptable-jmx-exporter. Please report an issue on GitHub.", _name), th);
			}
		});
	}

	private void fallbackAndHandleNonAttributeList(final ScrapeOutput<ScrapeRuleType> output, final CachedMBeanInfo info, final AttributeList obtainedAttributes, final long timestamp) {
		if (obtainedAttributes.size() != info.attributeNamesToGet.length) {
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE, String.format("MBeanServer#getAttributes(%s, %s) returned an AttributeList containing non-Attribute elemenets and the number of elements does not match.", info.name.objectName(), Arrays.toString(info.attributeNamesToGet)));
			return;
		}
		for (int i = 0; i < info.attributeNamesToGet.length; ++i) {
			final AttributeRule request = info.requests.get(info.attributeNamesToGet[i]);
			if (request == null) {
				if (LOG.isLoggable(Level.WARNING))
					LOG.log(Level.WARNING, String.format("This is not expected happen. The attribute \"{}\" could not be found in requests.", info.attributeNamesToGet[i]));
				continue;
			}
			Object value = obtainedAttributes.get(i);
			if (value instanceof Attribute) {
				value = ((Attribute) value).getValue();
			}
			final Sample<ScrapeRuleType> sample = new Sample<>(request.ruleMatch.rule, request.ruleMatch.captures, timestamp, info.name, info.info, request.attribute, value);
			safeEmit(output, sample);
		}
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output, final ObjectName _name) throws InstanceNotFoundException {
		final CachedMBeanInfo info;
		try {
			info = mbeanInfoCache.get(_name);
		} catch (final Throwable th) {
			if (th.getCause() instanceof InstanceNotFoundException)
				throw (InstanceNotFoundException) th.getCause();
			if (LOG.isLoggable(Level.WARNING))
				LOG.log(Level.WARNING, String.format("Got an unexpected exception while collecting MBean information of \"%s\". This may be a bug in scriptable-jmx-exporter. Please report an issue on GitHub.", _name), th);
			return;
		}
		if (info == MBEAN_INFO_NEGATIVE_CACHE)
			return;

		final long timestamp = System.currentTimeMillis();
		final AttributeList obtainedAttributes;
		try {
			// NOTE: We assume the results are not always in the same order as the arguments as the javadoc
			// doesn't say anything about it.
			obtainedAttributes = server.getAttributes(_name, info.attributeNamesToGet);
		} catch (final Throwable th) {
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE, String.format("MBeanServer#getAttributes(%s, %s) #=> %s. The MBean is banned for 1 minutes.", _name, Arrays.toString(info.attributeNamesToGet), th.getClass().getSimpleName()), th);
			mbeanInfoCache.put(_name, MBEAN_INFO_NEGATIVE_CACHE);
			return;
		}
		if (obtainedAttributes == null) {
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE, String.format("MBeanServer#getAttributes(%s, %s) #=> null. The MBean is banned for 1 minutes.", _name, Arrays.toString(info.attributeNamesToGet)));
			mbeanInfoCache.put(_name, MBEAN_INFO_NEGATIVE_CACHE);
			return;
		}

		final List<Attribute> obtainedAttributeList;
		try {
			obtainedAttributeList = obtainedAttributes.asList();
		} catch (final IllegalArgumentException e) {
			// The AttributeList contains non-Attribute elements.
			fallbackAndHandleNonAttributeList(output, info, obtainedAttributes, timestamp);
			return;
		}

		int successfulAttributes = 0; // # of successfully obtained attributes
		for (final Attribute attribute : obtainedAttributeList) {
			final AttributeRule request = info.requests.get(attribute.getName());
			if (request == null) {
				if (LOG.isLoggable(Level.FINE))
					LOG.log(Level.FINE, String.format("MBeanServer#getAttributes(%s, %s) returned an attribute named \"%s\" which we didn't request. This indicates a bug in the MBean. Ignored.", _name, info.attributeNamesToGet, attribute.getName()));
				continue;
			}
			++successfulAttributes;
			final Sample<ScrapeRuleType> sample = new Sample<>(request.ruleMatch.rule, request.ruleMatch.captures, timestamp, info.name, info.info, request.attribute, attribute.getValue());
			safeEmit(output, sample);
		}

		if (successfulAttributes != info.attributeNamesToGet.length) {
			// Some of the attributes could not be retrieved. We need to check which attributes are missing.
			int i = 0;
			final Map<String, AttributeRule> requests = new HashMap<>(info.requests);
			final String[] successfulAttributeNames = new String[successfulAttributes];
			for (final Attribute attribute : obtainedAttributeList) {
				final AttributeRule request = requests.remove(attribute.getName());
				if (request == null)
					continue;
				successfulAttributeNames[i++] = attribute.getName();
			}

			// If `requests` still has elements at this point, they are the attributes that we couldn't obtain the value of.
			requests.forEach((attributeName, request) -> {
				// Let's probe why the attribute could not be obtained.
				try {
					final Object value = server.getAttribute(_name, attributeName);
					if (LOG.isLoggable(Level.FINE))
						LOG.log(Level.FINE, String.format("MBeanServer#getAttribute(%s, %s) #=> %s, while expecting an exception. This is weird. Anyway, the attribute will be banned for 1 minutes.", _name, attributeName, value));
				} catch (final Throwable th) {
					if (th.getCause() instanceof UnsupportedOperationException) {
						// Disable attribute for 10 minutes, iff unsupported.
						banAttribute(_name, attributeName);
						if (LOG.isLoggable(Level.FINE))
							LOG.log(Level.FINE, String.format("MBeanServer#getAttribute(%s, %s) #=> %s. The attribute will be banned for 10 minutes.", _name, attributeName, th.getClass().getSimpleName()), th);
					} else {
						if (LOG.isLoggable(Level.FINE))
							LOG.log(Level.FINE, String.format("MBeanServer#getAttribute(%s, %s) #=> %s. The attribute will be banned for 1 minutes.", _name, attributeName, th.getClass().getSimpleName()), th);
					}
				}
			});

			// Anyway, exclude failed attribute for 1 minutes.
			mbeanInfoCache.put(_name, new CachedMBeanInfo(info.name, info.info, info.requests, successfulAttributeNames));
		}
	}

	private String safeFormatValue(final Object value) {
		if (value == null)
			return null;
		try {
			return value.toString();
		} catch (final Throwable th) {
			return "<failed to format value>";
		}
	}

	private void safeEmit(final ScrapeOutput<ScrapeRuleType> output, final Sample<ScrapeRuleType> sample) {
		try {
			output.emit(sample);
		} catch (final Throwable th) {
			if (LOG.isLoggable(Level.WARNING))
				LOG.log(Level.WARNING, String.format("The callback raised an exception, while processing an MBean attribute: name = %s, attribute = %s, type = %s, value = %s. This indicates a bug in the transform script.", sample.name, sample.attribute.getName(), sample.attribute.getType(), safeFormatValue(sample.value)), th);
		}
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
