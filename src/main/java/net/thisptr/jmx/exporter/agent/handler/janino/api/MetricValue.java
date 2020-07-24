package net.thisptr.jmx.exporter.agent.handler.janino.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thisptr.jmx.exporter.agent.misc.StringWriter;

/**
 * This class represents a single Prometheus metric sample.
 * 
 * <p>
 * Jackson annotations is added only to implement toString() for logging and debugging.
 * Do not expect consistent serialization/deserialization behavior.
 * JSON field names are subject to change without any notice, unlike Java field names which are part of the
 * Java scripting interface.
 * </p>
 */
public class MetricValue {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Name of the metric.
	 */
	@JsonProperty("name")
	public String name;

	/*
	 * THIS FIELD IS NOT PART OF THE API. DO NOT USE FROM TRANSFORM SCRIPTS.
	 */
	@JsonIgnore
	/* package private */ StringWriter nameWriter;

	@JsonProperty("suffix")
	public String suffix;

	/**
	 * Labels.
	 */
	@JsonProperty("labels")
	public Map<String, String> labels;

	/**
	 * Metric value.
	 */
	@JsonProperty("value")
	public double value;

	/**
	 * Time in milliseconds at which the value is scraped. This timestamp is included in the final /metrics response when
	 * include_timestamp option is enabled and the timestamp is not 0.
	 */
	@JsonProperty("timestamp")
	@JsonInclude(Include.NON_DEFAULT)
	public long timestamp = 0;

	/**
	 * If set and include_help is enabled, this value is included as HELP metadata in the final /metrics response.
	 */
	@JsonProperty("help")
	@JsonInclude(Include.NON_NULL)
	public String help;

	/**
	 * If set and include_type is enabled, this value is included as TYPE metadata in the final /metrics response.
	 */
	@JsonProperty("type")
	@JsonInclude(Include.NON_NULL)
	public String type;

	@Override
	public String toString() {
		try {
			return MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e); // not expected to happen
		}
	}
}