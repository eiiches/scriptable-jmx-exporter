package net.thisptr.java.prometheus.metrics.agent;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrometheusMetric {

	@JsonProperty("name")
	public String name;

	@JsonProperty("labels")
	public Map<String, String> labels;

	@JsonProperty("value")
	public double value;

	@JsonProperty("timestamp")
	public long timestamp = 0;

	@JsonProperty("help")
	public String help;
}
