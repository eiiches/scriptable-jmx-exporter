package net.thisptr.jmx.exporter.agent.scripting.janino.internal;

public class MetricNamer {
	private char separator = ':';
	private final StringBuilder builder;

	public MetricNamer(final int expectedSize) {
		this.builder = new StringBuilder(expectedSize);
	}

	public void separator(final char separator) {
		this.separator = separator;
	}

	public int push(final String name) {
		final int length = builder.length();
		if (length != 0)
			builder.append(separator);
		builder.append(name);
		return length;
	}

	public void pop(final int mark) {
		builder.setLength(mark);
	}

	@Override
	public String toString() {
		return builder.toString();
	}

	/**
	 * This will not reset separator to default value, unlike {@link #reset()}.
	 */
	public void clear() {
		builder.setLength(0);
	}

	/**
	 * Reset this instance to the initial state. The separator is reset to default value.
	 */
	public void reset() {
		clear();
		separator = ':';
	}

	public char separator() {
		return separator;
	}
}
