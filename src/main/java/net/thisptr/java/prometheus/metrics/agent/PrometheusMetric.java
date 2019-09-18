package net.thisptr.java.prometheus.metrics.agent;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

public class PrometheusMetric {

	@JsonProperty("name")
	public String name;

	@JsonProperty("labels")
	public Map<String, String> labels;

	@JsonProperty("value")
	public double value;

	@JsonProperty("timestamp")
	@JsonInclude(Include.NON_NULL)
	public Long timestamp = null;

	public static PrometheusMetric fromJsonNode(final JsonNode tree) {
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
				m.labels.put(entry.getKey(), entry.getValue().asText());
			}
		} else {
			m.labels = null;
		}

		return m;
	}
}