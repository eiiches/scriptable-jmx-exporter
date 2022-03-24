package net.thisptr.jmx.exporter.agent.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternAndCaptures {
	private static final Pattern NAMED_CAPTURE_PATTERN = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

	private final Pattern pattern;
	private final String[] capturesNames;

	private PatternAndCaptures(final Pattern pattern, final String[] captureNames) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(capturesNames);
		result = prime * result + ((pattern == null) ? 0 : pattern.toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PatternAndCaptures other = (PatternAndCaptures) obj;
		if (!Arrays.equals(capturesNames, other.capturesNames))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.toString().equals(other.pattern.toString()))
			return false;
		return true;
	}
}
