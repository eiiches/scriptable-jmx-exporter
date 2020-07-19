package net.thisptr.jmx.exporter.agent.misc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.ObjectName;

import com.google.common.annotations.VisibleForTesting;

import net.thisptr.jmx.exporter.agent.javacc.AttributeNamePatternParser;

public class AttributeNamePattern {
	public final Pattern domain;
	public final Map<String, Pattern> keys;
	public final Pattern attribute;

	public AttributeNamePattern(final String domain, final Map<String, String> keys, final String attribute) {
		this.domain = domain != null ? Pattern.compile(domain) : null;
		final Map<String, Pattern> tmp = new LinkedHashMap<>();
		keys.forEach((k, v) -> {
			tmp.put(k, Pattern.compile(v));
		});
		this.keys = Collections.unmodifiableMap(tmp);
		this.attribute = attribute != null ? Pattern.compile(attribute) : null;
	}

	public boolean matches(final String domainToTest, final Map<String, String> keyPropertiesToTest, final String attribute) {
		if (!nameMatches(domainToTest, keyPropertiesToTest))
			return false;

		if (this.attribute != null && (attribute == null || !this.attribute.matcher(attribute).matches()))
			return false;

		return true;
	}

	@VisibleForTesting
	boolean matches(final ObjectName name_, final String attribute) {
		final FastObjectName name = new FastObjectName(name_);
		return matches(name.domain(), name.keyProperties(), attribute);
	}

	public static AttributeNamePattern compile(final String patternText) {
		return AttributeNamePatternParser.parse(patternText);
	}

	public boolean nameMatches(final String domainToTest, final Map<String, String> keyPropertiesToTest) {
		if (domain != null && !domain.matcher(domainToTest).matches())
			return false;

		for (final Map.Entry<String, Pattern> patternEntry : keys.entrySet()) {
			String targetValue = keyPropertiesToTest.get(patternEntry.getKey());
			if (targetValue == null)
				return false;
			if (!patternEntry.getValue().matcher(targetValue).matches())
				return false;
		}

		return true;
	}
}
