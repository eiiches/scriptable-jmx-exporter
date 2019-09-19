package net.thisptr.java.prometheus.metrics.agent;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

public class PrometheusMetricWriter implements Closeable {
	private final StringBuilder builder;
	private final boolean includeTimestamp;

	public PrometheusMetricWriter(final StringBuilder builder, final boolean includeTimestamp) {
		this.builder = builder;
		this.includeTimestamp = includeTimestamp;
	}

	/**
	 * A metric name must match [a-zA-Z_:][a-zA-Z0-9_:]*.
	 *
	 * @param name
	 * @return
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeLabelName(String)}
	 */
	private static void sanitizeMetricName(final StringBuilder builder, final String name) {
		if (name.isEmpty()) {// An empty name is not allowed.
			builder.append('_');
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
				builder.append(ch);
			} else {
				builder.append('_');
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
	 *
	 * @see https://prometheus.io/docs/concepts/data_model/
	 * @see {@link #sanitizeMetricName(String)}
	 */
	private static void sanitizeLabelName(final StringBuilder builder, final String name) {
		if (name.isEmpty()) { // An empty name is not allowed.
			builder.append('_');
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
				builder.append(ch);
			} else {
				builder.append('_');
			}
		}
	}

	public void write(final PrometheusMetric metric) throws IOException {
		sanitizeMetricName(builder, metric.name);
		if (!metric.labels.isEmpty()) {
			builder.append('{');
			for (final Entry<String, JsonNode> entry : metric.labels.entrySet()) {
				sanitizeLabelName(builder, entry.getKey());
				builder.append('=');
				sanitizeLabelValue(builder, entry.getValue());
				builder.append(',');
			}
			builder.append('}');
		}
		builder.append(' ');
		builder.append(metric.value);
		if (includeTimestamp && metric.timestamp != null) {
			builder.append(' ');
			builder.append(metric.timestamp);
		}
		builder.append('\n');
	}

	private static void sanitizeLabelValue(final StringBuilder builder, final JsonNode value) {
		if (value == null || value.isNull()) {
			builder.append('"');
			builder.append("null");
			builder.append('"');
		} else {
			builder.append('"');
			final String text = value.isTextual() ? value.asText() : value.toString();
			final int length = text.length();
			for (int i = 0; i < length; ++i) {
				final char ch = text.charAt(i);
				switch (ch) {
				case '\\':
					builder.append('\\');
					builder.append('\\');
					break;
				case '\n':
					builder.append('\\');
					builder.append('\n');
					break;
				case '"':
					builder.append('\\');
					builder.append('\"');
					break;
				default:
					builder.append(ch);
				}
			}
			builder.append('"');
		}
	}

	@Override
	public void close() throws IOException {}
}
