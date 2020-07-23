package net.thisptr.jmx.exporter.agent;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

import net.thisptr.jmx.exporter.agent.utils.MoreLongs;

public class PrometheusMetricWriter implements Closeable {
	private final boolean includeTimestamp;

	private ByteBuffer buf;
	private byte[] bytes;
	private int position;

	private final WritableByteChannel channel;
	private final WritableByteChannelController controller;

	public interface WritableByteChannelController {
		public void awaitWritable() throws IOException;
	}

	public PrometheusMetricWriter(final WritableByteChannel channel, final WritableByteChannelController controller, final ByteBuffer buf, final boolean includeTimestamp) {
		if (!buf.hasArray())
			throw new IllegalArgumentException("buf must be an array-backed ByteBuffer");
		this.channel = channel;
		this.controller = controller;
		this.buf = buf;
		this.bytes = buf.array();
		this.position = 0;
		this.includeTimestamp = includeTimestamp;
	}

	private void flush() throws IOException {
		buf.position(0);
		buf.limit(position);
		while (true) {
			channel.write(buf);
			if (!buf.hasRemaining())
				break;
			controller.awaitWritable();
		}
	}

	/**
	 * Tries to allocate the specified length from the buffer.
	 * If there is not enough space to accommodate in the buffer, the buffer is flushed
	 * and then the request is fulfilled at the start of the buffer.
	 * 
	 * @param length the number of bytes to allocate.
	 * @return the index into the buffer the caller should start writing at.
	 * @throws IOException
	 */
	private int ensureAtLeast(final int length) throws IOException {
		final int index = this.position;
		final byte[] bytes = this.bytes;
		if (bytes.length - index < length) {
			flush();
			this.position = 0;
			if (bytes.length < length) { // the buffer is too small
				// allocate a new buffer large enough to accommodate next writes
				this.buf = ByteBuffer.allocate(length);
				this.bytes = buf.array();
				return 0;
			}
			return 0;
		}
		return index;
	}

	/**
	 * Writes out a metric name into byte[] at the specified index. Invalid characters are
	 * silently replaced by '_' (underscore). If the metric name is empty,
	 * this method just writes a single '_' (underscore).
	 * 
	 * <p>
	 * A metric name must match [a-zA-Z_:][a-zA-Z0-9_:]*.
	 * </p>
	 * 
	 * <p>
	 * It's caller's responsibility to ensure the byte[] has enough space. This method requires at most <tt>Math.max(1, name.length())</tt> bytes.
	 * </p>
	 *
	 * @param name
	 * @return the next index after the metric name is written.
	 * @throws IOException
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeLabelName(String)}
	 */
	static int sanitizeMetricName(final byte[] bytes, int index, final String name) {
		final int length = name.length();
		if (length == 0) {// An empty name is not allowed.
			bytes[index++] = '_';
			return index;
		}
		for (int i = 0; i < length; ++i) {
			final char ch = name.charAt(i);
			final boolean valid = ('a' <= ch && ch <= 'z')
					|| (ch == '_')
					|| (ch == ':')
					|| ('A' <= ch && ch <= 'Z')
					|| ('0' <= ch && ch <= '9' && i != 0);
			if (valid) {
				bytes[index++] = (byte) ch;
			} else {
				if (Character.isHighSurrogate(ch))
					++i;
				bytes[index++] = '_';
			}
		}
		return index;
	}

	/**
	 * A label name must match [a-zA-Z_][a-zA-Z0-9_]*.
	 *
	 * We've implemented this method using simple {@link java.util.regex.Pattern#compile(String)}
	 * and {@link java.util.regex.Matcher#replaceAll(String)} once, but it turned out to be the
	 * bottle-neck during load testing with wrk.
	 * 
	 * <p>
	 * This method requires at most <tt>Math.max(1, name.length())</tt> bytes. The actual size depends on the number of non-BMP characters in the name.
	 * </p>
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 *
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeMetricName(String)}
	 */
	private static int sanitizeLabelName(final byte[] bytes, int index, final String name) {
		if (name.isEmpty()) { // An empty name is not allowed.
			bytes[index++] = '_';
			return index;
		}
		final int length = name.length();
		for (int i = 0; i < length; ++i) {
			final char ch = name.charAt(i);
			final boolean valid = ('a' <= ch && ch <= 'z')
					|| ('A' <= ch && ch <= 'Z')
					|| ('0' <= ch && ch <= '9' && i != 0)
					|| (ch == '_');
			if (valid) {
				bytes[index++] = (byte) ch;
			} else {
				if (Character.isHighSurrogate(ch))
					++i;
				bytes[index++] = '_';
			}
		}
		return index;
	}

	public void write(final PrometheusMetric metric) throws IOException {
		ensureAtLeast(Math.max(1, metric.name.length()) + 1 /* { */);
		this.position = sanitizeMetricName(bytes, this.position, metric.name);

		if (metric.labels != null && !metric.labels.isEmpty()) {
			bytes[this.position++] = '{';
			metric.labels.forEach((labelName, labelValue) -> {
				final int size = 2 /* = and , */
						+ Math.max(1, labelName.length()) /* label name */
						+ (labelValue != null ? 2 + labelValue.length() * 3 : 6) /* label value */
						+ 1; /* } */
				try {
					ensureAtLeast(size);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				int index = this.position;
				index = sanitizeLabelName(bytes, index, labelName);
				bytes[index++] = '=';
				index = sanitizeLabelValue(bytes, index, labelValue);
				bytes[index++] = ',';
				this.position = index;
			});
			bytes[this.position++] = '}';
		}

		if (metric.value == (long) metric.value) {
			ensureAtLeast(1 /* ' ' */ + 20 + 1 /* \n */);
			bytes[this.position++] = ' ';
			this.position = MoreLongs.writeAsString((long) metric.value, bytes, this.position);
		} else {
			final String valueText = String.valueOf(metric.value); // TODO: avoid string allocation
			ensureAtLeast(1 /* ' ' */ + valueText.length() + 1 /* \n */);
			bytes[this.position++] = ' ';
			this.position = writeTextUtf8(bytes, this.position, valueText);
		}

		if (includeTimestamp && metric.timestamp != 0) {
			ensureAtLeast(1 /* ' ' */ + 20 + 1 /* \n */); // Long.MIN_VALUE is 20 chars long.
			bytes[this.position++] = ' ';
			this.position = MoreLongs.writeAsString(metric.timestamp, bytes, this.position);
		}

		bytes[this.position++] = '\n';
	}

	/**
	 * <p>
	 * This method requires at most <tt>text.length() * 3 + 2</tt> bytes if text is not null. If text is null 6 bytes.
	 * </p>
	 * 
	 * @param bytes
	 * @param index
	 * @param text
	 * @return
	 * @throws IOException
	 */
	private static int sanitizeLabelValue(final byte[] bytes, int index, final String text) {
		bytes[index++] = '"';
		if (text == null) {
			bytes[index++] = 'n';
			bytes[index++] = 'u';
			bytes[index++] = 'l';
			bytes[index++] = 'l';
		} else {
			index = writeTextUtf8Escaped(bytes, index, text, true);
		}
		bytes[index++] = '"';
		return index;
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	private static final byte[] HELP = "HELP".getBytes(StandardCharsets.UTF_8);
	private static final byte[] TYPE = "TYPE".getBytes(StandardCharsets.UTF_8);

	/**
	 * <p>
	 * This method requires at most <tt>text.length() * 3</tt>.
	 * A single char (U+0000 - U+FFFF) takes at most 3 bytes in UTF-8.
	 * A single surrogate codepoint (two chars in UTF-16, >U+FFFF) could take up to 4 bytes in UTF-8.
	 * </p>
	 * 
	 * @param bytes
	 * @param index
	 * @param text
	 * @throws IOException
	 */
	private static int writeTextUtf8(final byte[] bytes, int index, final String text) throws IOException {
		final int length = text.length();
		for (int i = 0; i < length; ++i) {
			final char ch = text.charAt(i);
			if (ch < 0x80) {
				bytes[index++] = (byte) ch;
			} else if (Character.isHighSurrogate(ch)) {
				final int codePoint = Character.toCodePoint(ch, text.charAt(++i));
				index = writeUnicode(bytes, index, codePoint);
			} else {
				index = writeUnicode(bytes, index, ch);
			}
		}
		return index;
	}

	/**
	 * <p>
	 * This method requires at most <tt>text.length() * 3</tt> bytes.
	 * </p>
	 * 
	 * @param bytes
	 * @param index
	 * @param text
	 * @param escapeDoubleQuotes
	 * @throws IOException
	 */
	private static int writeTextUtf8Escaped(final byte[] bytes, int index, final String text, final boolean escapeDoubleQuotes) {
		final int length = text.length();
		for (int i = 0; i < length; ++i) {
			final char ch = text.charAt(i);
			switch (ch) {
			case '\\':
				bytes[index++] = '\\';
				bytes[index++] = '\\';
				break;
			case '\n':
				bytes[index++] = '\\';
				bytes[index++] = 'n';
				break;
			case '"':
				if (escapeDoubleQuotes)
					bytes[index++] = '\\';
				bytes[index++] = '"';
				break;
			default:
				if (Character.isHighSurrogate(ch)) {
					final int codePoint = Character.toCodePoint(ch, text.charAt(++i));
					// at most 4 bytes
					index = writeUnicode(bytes, index, codePoint);
				} else {
					// at most 3 bytes
					index = writeUnicode(bytes, index, ch);
				}
			}
		}
		return index;
	}

	private void writeAnnotation(final String metricName, final byte[] annotationType, final String value) throws IOException {
		final int size = 5 + annotationType.length + Math.max(1, metricName.length()) + value.length() * 3;
		ensureAtLeast(size);
		int index = this.position;
		bytes[index++] = '#';
		bytes[index++] = ' ';
		for (int i = 0; i < annotationType.length; ++i)
			bytes[index++] = annotationType[i];
		bytes[index++] = ' ';
		index = sanitizeMetricName(bytes, index, metricName);
		bytes[index++] = ' ';
		index = writeTextUtf8Escaped(bytes, index, value, false);
		bytes[index++] = '\n';
		this.position = index;
	}

	public void writeHelp(final String name, final String helpText) throws IOException {
		writeAnnotation(name, HELP, helpText);
	}

	public void writeType(final String name, final String typeText) throws IOException {
		writeAnnotation(name, TYPE, typeText);
	}

	private static int writeUnicode(final byte[] bytes, int index, final int codePoint) {
		// https://en.wikipedia.org/wiki/UTF-8#Description
		if (codePoint < 0x80) {
			bytes[index++] = (byte) codePoint;
		} else if (codePoint < 0x800) {
			bytes[index++] = (byte) (0b1100_0000 | codePoint >>> 6);
			bytes[index++] = (byte) (0b1000_0000 | (codePoint >>> 0) & 0b0011_1111);
		} else if (codePoint < 0x10000) {
			bytes[index++] = (byte) (0b1110_0000 | (codePoint >>> 12));
			bytes[index++] = (byte) (0b1000_0000 | (codePoint >>> 6) & 0b0011_1111);
			bytes[index++] = (byte) (0b1000_0000 | (codePoint >>> 0) & 0b0011_1111);
		} else if (codePoint < 0x110000) {
			bytes[index++] = (byte) (0b1111_0000 | (codePoint >>> 18));
			bytes[index++] = (byte) (0b1000_0000 | (codePoint >>> 12) & 0b0011_1111);
			bytes[index++] = (byte) (0b1000_0000 | (codePoint >>> 6) & 0b0011_1111);
			bytes[index++] = (byte) (0b1000_0000 | (codePoint >>> 0) & 0b0011_1111);
		} else {
			throw new IllegalArgumentException("invalid codepoint: " + codePoint);
		}
		return index;
	}
}
