package net.thisptr.java.prometheus.metrics.agent.misc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.ObjectName;

import net.thisptr.java.prometheus.metrics.agent.javacc.AttributeNamePatternParser;

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

	public boolean matches(final ObjectName name, final String attribute) {
		if (!nameMatches(name))
			return false;

		if (this.attribute != null && (attribute == null || !this.attribute.matcher(attribute).matches()))
			return false;

		return true;
	}

	public static AttributeNamePattern compile(final String patternText) {
		return AttributeNamePatternParser.parse(patternText);
	}

	public boolean nameMatches(final ObjectName name) {
		if (domain != null && !domain.matcher(name.getDomain()).matches())
			return false;

		final Map<String, String> target = name.getKeyPropertyList();
		for (final Map.Entry<String, Pattern> patternEntry : keys.entrySet()) {
			String targetValue = target.get(patternEntry.getKey());
			if (targetValue == null)
				return false;
			if (targetValue.startsWith("\""))
				targetValue = ObjectName.unquote(targetValue);
			if (!patternEntry.getValue().matcher(targetValue).matches())
				return false;
		}

		return true;
	}
}