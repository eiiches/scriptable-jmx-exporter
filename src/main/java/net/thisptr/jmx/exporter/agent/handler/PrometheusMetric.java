package net.thisptr.jmx.exporter.agent.handler;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.thisptr.jmx.exporter.agent.misc.StringWriter;

public class PrometheusMetric {

	@JsonProperty("name")
	public String name;

	@JsonProperty("suffix")
	public String suffix;

	@JsonProperty("labels")
	public Map<String, String> labels;

	@JsonProperty("value")
	public double value;

	@JsonProperty("timestamp")
	public long timestamp = 0;

	@JsonProperty("help")
	public String help;

	@JsonProperty("type")
	public String type;

	@JsonIgnore
	public StringWriter nameWriter;
}
