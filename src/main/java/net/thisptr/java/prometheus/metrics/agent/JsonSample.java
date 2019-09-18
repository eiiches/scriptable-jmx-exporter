package net.thisptr.java.prometheus.metrics.agent;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonSample {
	@JsonProperty("type")
	public String type;

	@JsonProperty("value")
	public JsonNode value;

	@JsonProperty("domain")
	public String domain;

	@JsonProperty("properties")
	public Map<String, String> properties;

	@JsonProperty("timestamp")
	public long timestamp;

	@JsonProperty("attribute")
	public String attribute;
}