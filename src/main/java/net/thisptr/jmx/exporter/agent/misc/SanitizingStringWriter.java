package net.thisptr.jmx.exporter.agent.misc;

/**
 * Writes out a metric name into byte[] at the specified index. Invalid characters are
 * silently replaced by '_' (underscore). If the metric name is empty,
 * this method just writes a single '_' (underscore). If the name starts with a number, '_' is prepended.
 *
 * <p>
 * A metric name must match [a-zA-Z_:][a-zA-Z0-9_:]*.
 * </p>
 *
 * <p>
 * It's caller's responsibility to ensure the byte[] has enough space. This method requires at most <tt>name.length() + 1</tt> bytes.
 * </p>
 *
 * @param name
 * @return the next index after the metric name is written.
 * @throws IOException
 * @see https://prometheus.io/docs/concepts/data_model/
 * @see {@link #sanitizeLabelName(String)}
 */
public class SanitizingStringWriter implements StringWriter {
	private static final SanitizingStringWriter INSTANCE = new SanitizingStringWriter();

	public static SanitizingStringWriter getInstance() {
		return INSTANCE;
	}

	@Override
	public int expectedSize(final String name) {
		return name.length() + 1;
	}

	@Override
	public int write(final String name, final byte[] bytes, int index) {
		final int savedIndex = index;
		final int length = name.length();
		if (length == 0) {// An empty name is not allowed.
			bytes[index++] = '_';
			return index;
		}
		for (int i = 0; i < length; ++i) {
			final char ch = name.charAt(i);
			if (('a' <= ch && ch <= 'z')
					|| (ch == '_')
					|| (ch == ':')
					|| ('A' <= ch && ch <= 'Z')) {
				bytes[index++] = (byte) ch;
			} else if ('0' <= ch && ch <= '9') {
				if (index == savedIndex)
					bytes[index++] = '_';
				bytes[index++] = (byte) ch;
			} else {
				if (Character.isHighSurrogate(ch))
					++i;
				bytes[index++] = '_';
			}
		}
		return index;
	}
}
