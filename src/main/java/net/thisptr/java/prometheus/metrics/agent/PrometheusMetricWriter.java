package net.thisptr.java.prometheus.metrics.agent;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

public class PrometheusMetricWriter implements Closeable {
	private final OutputStream os;
	private final boolean includeTimestamp;

	public PrometheusMetricWriter(final OutputStream os, final boolean includeTimestamp) {
		this.os = os;
		this.includeTimestamp = includeTimestamp;
	}

	/**
	 * A metric name must match [a-zA-Z_:][a-zA-Z0-9_:]*.
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeLabelName(String)}
	 */
	private static void sanitizeMetricName(final OutputStream os, final String name) throws IOException {
		if (name.isEmpty()) {// An empty name is not allowed.
			os.write('_');
			return;
		}
		final int length = name.length();
		for (int i = 0; i < length; ++i) {
			final char ch = name.charAt(i);
			final boolean valid = ('a' <= ch && ch <= 'z')
					|| ('A' <= ch && ch <= 'Z')
					|| ('0' <= ch && ch <= '9' && i != 0)
					|| (ch == '_')
					|| (ch == ':');
			if (valid) {
				os.write(ch);
			} else {
				os.write('_');
			}
		}
	}

	/**
	 * A label name must match [a-zA-Z_][a-zA-Z0-9_]*.
	 *
	 * Once we implemented this method using simple {@link java.util.regex.Pattern#compile(String)}
	 * and {@link java.util.regex.Matcher#replaceAll(String)}, but it turned out to be the
	 * bottle-neck during load testing with wrk.
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 *
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeMetricName(String)}
	 */
	private static void sanitizeLabelName(final OutputStream os, final String name) throws IOException {
		if (name.isEmpty()) { // An empty name is not allowed.
			os.write('_');
			return;
		}
		final int length = name.length();
		for (int i = 0; i < length; ++i) {
			final char ch = name.charAt(i);
			final boolean valid = ('a' <= ch && ch <= 'z')
					|| ('A' <= ch && ch <= 'Z')
					|| ('0' <= ch && ch <= '9' && i != 0)
					|| (ch == '_');
			if (valid) {
				os.write(ch);
			} else {
				os.write('_');
			}
		}
	}

	public void write(final PrometheusMetric metric) throws IOException {
		sanitizeMetricName(os, metric.name);
		if (!metric.labels.isEmpty()) {
			os.write('{');
			for (final Entry<String, String> entry : metric.labels.entrySet()) {
				sanitizeLabelName(os, entry.getKey());
				os.write('=');
				sanitizeLabelValue(os, entry.getValue());
				os.write(',');
			}
			os.write('}');
		}
		os.write(' ');
		writeTextUtf8(os, String.valueOf(metric.value)); // TODO: avoid string allocation
		if (includeTimestamp && metric.timestamp != 0) {
			os.write(' ');
			writeTextUtf8(os, String.valueOf(metric.timestamp)); // TODO: avoid string allocation
		}
		os.write('\n');
	}

	private static void sanitizeLabelValue(final OutputStream os, final String text) throws IOException {
		os.write('"');
		if (text == null) {
			os.write(NULL);
		} else {
			writeTextUtf8Escaped(text, os, true);
		}
		os.write('"');
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

	private static final byte[] HELP = "HELP".getBytes(StandardCharsets.UTF_8);
	private static final byte[] TYPE = "TYPE".getBytes(StandardCharsets.UTF_8);
	private static final byte[] NULL = "null".getBytes(StandardCharsets.UTF_8);

	private static void writeTextUtf8(final OutputStream os, final String text) throws IOException {
		final int length = text.length();
		for (int i = 0; i < length; ++i) {
			final char ch = text.charAt(i);
			if (ch < 0x80) {
				os.write((byte) ch);
			} else if (Character.isHighSurrogate(ch)) {
				final int codePoint = Character.toCodePoint(ch, text.charAt(++i));
				writeUnicode(os, codePoint);
			} else {
				writeUnicode(os, ch);
			}
		}
	}

	private static void writeTextUtf8Escaped(final String text, final OutputStream os, final boolean escapeDoubleQuotes) throws IOException {
		final int length = text.length();
		for (int i = 0; i < length; ++i) {
			final char ch = text.charAt(i);
			switch (ch) {
			case '\\':
				os.write('\\');
				os.write('\\');
				break;
			case '\n':
				os.write('\\');
				os.write('n');
				break;
			case '"':
				if (escapeDoubleQuotes)
					os.write('\\');
				os.write('"');
				break;
			default:
				if (Character.isHighSurrogate(ch)) {
					final int codePoint = Character.toCodePoint(ch, text.charAt(++i));
					writeUnicode(os, codePoint);
				} else {
					writeUnicode(os, ch);
				}
			}
		}
	}

	private void writeAnnotation(final String metricName, final byte[] annotationType, final String value) throws IOException {
		os.write('#');
		os.write(' ');
		os.write(annotationType);
		os.write(' ');
		sanitizeMetricName(os, metricName);
		os.write(' ');
		writeTextUtf8Escaped(value, os, false);
		os.write('\n');
	}

	public void writeHelp(final String name, final String helpText) throws IOException {
		writeAnnotation(name, HELP, helpText);
	}

	public void writeType(final String name, final String typeText) throws IOException {
		writeAnnotation(name, TYPE, typeText);
	}

	private static void writeUnicode(final OutputStream os, final int codePoint) throws IOException {
		// https://en.wikipedia.org/wiki/UTF-8#Description
		if (codePoint < 0x80) {
			os.write(codePoint);
		} else if (codePoint < 0x800) {
			os.write(0b1100_0000 | codePoint >>> 6);
			os.write(0x1000_0000 | (codePoint >>> 0) & 0b0011_1111);
		} else if (codePoint < 0x10000) {
			os.write(0b1110_0000 | (codePoint >>> 12));
			os.write(0x1000_0000 | (codePoint >>> 6) & 0b0011_1111);
			os.write(0x1000_0000 | (codePoint >>> 0) & 0b0011_1111);
		} else if (codePoint < 0x110000) {
			os.write(0b1111_0000 | (codePoint >>> 18));
			os.write(0x1000_0000 | (codePoint >>> 12) & 0b0011_1111);
			os.write(0x1000_0000 | (codePoint >>> 6) & 0b0011_1111);
			os.write(0x1000_0000 | (codePoint >>> 0) & 0b0011_1111);
		} else {
			throw new IllegalArgumentException("invalid codepoint: " + codePoint);
		}
	}
}
