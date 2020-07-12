package net.thisptr.java.prometheus.metrics.agent;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrometheusMetric {

	@JsonProperty("name")
	public String name;

	// TODO: Can we change this to Map<String, String> ?
	@JsonProperty("labels")
	public Map<String, String> labels;

	@JsonProperty("value")
	public double value;

	// TODO: I think we should change this to primitive long type to avoid boxing. We can use 0 to represent "unset" state.
	@JsonProperty("timestamp")
	@JsonInclude(Include.NON_NULL)
	public Long timestamp = null;

	@JsonProperty("help")
	public String help;
}