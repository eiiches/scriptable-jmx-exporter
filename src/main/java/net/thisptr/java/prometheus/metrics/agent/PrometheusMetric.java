package net.thisptr.java.prometheus.metrics.agent;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrometheusMetric {

	@JsonProperty("name")
	public String name;

	@JsonProperty("labels")
	public Map<String, String> labels;

	@JsonProperty("value")
	public double value;
}