package net.thisptr.jmx.exporter.agent.misc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.ObjectName;

import com.google.common.annotations.VisibleForTesting;

import net.thisptr.jmx.exporter.agent.javacc.AttributeNamePatternParser;

public class AttributeNamePattern {
	public final PatternAndCaptures domain;
	public final Map<String, PatternAndCaptures> keys;
	public final PatternAndCaptures attribute;

	public AttributeNamePattern(final String domain, final Map<String, String> keys, final String attribute) {
		this.domain = domain != null ? PatternAndCaptures.compile(domain) : null;
		final Map<String, PatternAndCaptures> tmp = new LinkedHashMap<>();
		keys.forEach((k, v) -> {
			tmp.put(k, PatternAndCaptures.compile(v));
		});
		this.keys = Collections.unmodifiableMap(tmp);
		this.attribute = attribute != null ? PatternAndCaptures.compile(attribute) : null;
	}

	/**
	 * Tests if the input matches to this pattern. Captured variables are added to `captures`.
	 * Even if the result is false, captures may contain partial results after call.
	 *
	 * @param domainToTest
	 * @param keyPropertiesToTest
	 * @param attribute
	 * @param captures
	 * @return
	 */
	public boolean matches(final String domainToTest, final Map<String, String> keyPropertiesToTest, final String attribute, final Map<String, String> captures) {
		if (!nameMatches(domainToTest, keyPropertiesToTest, captures))
			return false;

		if (this.attribute != null && (attribute == null || !this.attribute.matches(attribute, captures)))
			return false;

		return true;
	}

	@VisibleForTesting
	boolean matches(final ObjectName name_, final String attribute) {
		return matches(name_, attribute, null);
	}

	@VisibleForTesting
	boolean matches(final ObjectName name_, final String attribute, final Map<String, String> captures) {
		final FastObjectName name = new FastObjectName(name_);
		return matches(name.domain(), name.keyProperties(), attribute, captures);
	}

	public static AttributeNamePattern compile(final String patternText) {
		return AttributeNamePatternParser.parse(patternText);
	}

	public boolean nameMatches(final String domainToTest, final Map<String, String> keyPropertiesToTest, final Map<String, String> captures) {
		if (domain != null && !domain.matches(domainToTest, captures))
			return false;

		for (final Map.Entry<String, PatternAndCaptures> patternEntry : keys.entrySet()) {
			String targetValue = keyPropertiesToTest.get(patternEntry.getKey());
			if (targetValue == null)
				return false;
			if (!patternEntry.getValue().matches(targetValue, captures))
				return false;
		}

		return true;
	}
}
