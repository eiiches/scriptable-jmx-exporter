package net.thisptr.jmx.exporter.agent.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternAndCaptures {
	private static final Pattern NAMED_CAPTURE_PATTERN = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

	private final Pattern pattern;
	private final String[] capturesNames;

	public PatternAndCaptures(final Pattern pattern, final String[] captureNames) {
		this.pattern = pattern;
		this.capturesNames = captureNames;
	}

	public static PatternAndCaptures compile(final String regex) {
		final Pattern pattern = Pattern.compile(regex);
		final List<String> captures = new ArrayList<>();
		final Matcher m = NAMED_CAPTURE_PATTERN.matcher(regex);
		while (m.find())
			captures.add(m.group(1));
		return new PatternAndCaptures(pattern, captures.toArray(new String[0]));
	}

	public boolean matches(final CharSequence sequence) {
		return matches(sequence, null);
	}

	public boolean matches(final CharSequence sequence, final Map<String, String> captures) {
		final Matcher m = pattern.matcher(sequence);
		if (m.matches()) {
			if (captures != null) {
				for (final String captureName : capturesNames) {
					final String captureValue = m.group(captureName);
					if (captureValue != null)
						captures.put(captureName, captureValue);
				}
			}
			return true;
		}
		return false;
	}
}
