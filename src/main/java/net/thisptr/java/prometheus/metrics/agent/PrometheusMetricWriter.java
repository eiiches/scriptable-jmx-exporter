package net.thisptr.java.prometheus.metrics.agent;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.node.TextNode;

public class PrometheusMetricWriter implements Closeable {
	private final Writer writer;

	public PrometheusMetricWriter(final Writer writer) {
		this.writer = writer;
	}

	/**
	 * A metric name must match [a-zA-Z_:][a-zA-Z0-9_:]*.
	 *
	 * @param name
	 * @return
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeLabelName(String)}
	 */
	private static String sanitizeMetricName(final String name) {
		if (name.isEmpty()) // An empty name is not allowed.
			return "_";
		final char[] chars = name.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			final char ch = chars[i];
			final boolean valid = ('a' <= ch && ch <= 'z')
					|| ('A' <= ch && ch <= 'Z')
					|| ('0' <= ch && ch <= '9' && i != 0)
					|| (ch == '_')
					|| (ch == ':');
			if (!valid)
				chars[i] = '_';
		}
		return new String(chars);
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
	 *
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeMetricName(String)}
	 */
	private static String sanitizeLabelName(final String name) {
		if (name.isEmpty()) // An empty name is not allowed.
			return "_";
		final char[] chars = name.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			final char ch = chars[i];
			final boolean valid = ('a' <= ch && ch <= 'z')
					|| ('A' <= ch && ch <= 'Z')
					|| ('0' <= ch && ch <= '9' && i != 0)
					|| (ch == '_');
			if (!valid)
				chars[i] = '_';
		}
		return new String(chars);
	}

	public void write(final PrometheusMetric metric) throws IOException {
		writer.write(sanitizeMetricName(metric.name));
		if (!metric.labels.isEmpty()) {
			writer.write('{');
			for (final Entry<String, String> entry : metric.labels.entrySet()) {
				writer.write(sanitizeLabelName(entry.getKey()));
				writer.write('=');
				writer.write(TextNode.valueOf(entry.getValue()).toString());
				writer.write(',');
			}
			writer.write('}');
		}
		writer.write(' ');
		writer.write(String.valueOf(metric.value));
		writer.write('\n');
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
