package net.thisptr.java.prometheus.metrics.agent;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonSample {
	@JsonProperty("type")
	public String type;

	@JsonProperty("value")
	public JsonNode value;

	@JsonProperty("domain")
	public String domain;

	@JsonProperty("properties")
	public Map<String, JsonNode> properties;

	@JsonProperty("timestamp")
	public long timestamp;

	@JsonProperty("attribute")
	public String attribute;

	@JsonProperty("mbean_description")
	public String mbeanDescription;

	@JsonProperty("description")
	public String attributeDescription;

	public static JsonSample fromJsonNode(final JsonNode tree) {
		final JsonSample sample = new JsonSample();
		final JsonNode type = tree.get("type");
		sample.type = type != null ? type.asText() : null;
		sample.value = tree.get("value");
		sample.domain = tree.get("domain").asText();

		final JsonNode description = tree.get("description");
		sample.attributeDescription = description != null ? description.asText() : null;

		final JsonNode mbeanDescription = tree.get("mbean_description");
		sample.mbeanDescription = mbeanDescription != null ? mbeanDescription.asText() : null;

		final JsonNode properties = tree.get("properties");
		if (properties != null) {
			sample.properties = Maps.newHashMapWithExpectedSize(properties.size());
			final Iterator<Entry<String, JsonNode>> iter = properties.fields();
			while (iter.hasNext()) {
				final Entry<String, JsonNode> entry = iter.next();
				sample.properties.put(entry.getKey(), entry.getValue());
			}
		} else {
			sample.properties = null;
		}

		final JsonNode timestamp = tree.get("timestamp");
		sample.timestamp = timestamp != null ? timestamp.asLong() : 0L;
		sample.attribute = tree.get("attribute").asText();
		return sample;
	}
}