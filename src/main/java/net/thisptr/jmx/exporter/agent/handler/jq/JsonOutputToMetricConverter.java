package net.thisptr.jmx.exporter.agent.handler.jq;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import net.thisptr.jmx.exporter.agent.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.misc.Converter;

public class JsonOutputToMetricConverter implements Converter<JsonNode, PrometheusMetric> {
	private static final JsonOutputToMetricConverter INSTANCE = new JsonOutputToMetricConverter();

	public static JsonOutputToMetricConverter getInstance() {
		return INSTANCE;
	}

	@Override
	public PrometheusMetric convert(final JsonNode tree) {
		final PrometheusMetric m = new PrometheusMetric();

		final JsonNode name = tree.get("name");
		m.name = name != null ? name.asText() : null;

		final JsonNode value = tree.get("value");
		m.value = value != null ? value.asDouble() : 0L;

		final JsonNode timestamp = tree.get("timestamp");
		m.timestamp = timestamp != null ? timestamp.asLong() : null;

		final JsonNode labels = tree.get("labels");
		if (labels != null) {
			m.labels = Maps.newHashMapWithExpectedSize(labels.size());
			final Iterator<Entry<String, JsonNode>> iter = labels.fields();
			while (iter.hasNext()) {
				final Entry<String, JsonNode> entry = iter.next();

				final String labelValue;
				if (entry.getValue() == null) {
					labelValue = null;
				} else if (entry.getValue().isTextual()) {
					labelValue = entry.getValue().asText();
				} else {
					labelValue = entry.getValue().toString();
				}
				m.labels.put(entry.getKey(), labelValue);
			}
		} else {
			m.labels = null;
		}

		final JsonNode help = tree.get("help");
		m.help = help.asText();

		return m;
	}

}
