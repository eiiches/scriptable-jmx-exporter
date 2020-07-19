package net.thisptr.jmx.exporter.agent.config;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.net.HostAndPort;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jmx.exporter.agent.handler.Script;
import net.thisptr.jmx.exporter.agent.jackson.serdes.AttributeNamePatternDeserializer;
import net.thisptr.jmx.exporter.agent.jackson.serdes.HostAndPortDeserializer;
import net.thisptr.jmx.exporter.agent.jackson.serdes.LabelsDeserializer;
import net.thisptr.jmx.exporter.agent.jackson.serdes.ScriptDeserializer;
import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;
import net.thisptr.jmx.exporter.agent.scraper.ScrapeRule;

public class Config {

	@Valid
	@NotNull
	@JsonProperty("server")
	public ServerConfig server = new ServerConfig();

	public static class ServerConfig {

		@NotNull
		@JsonProperty("bind_address")
		@JsonDeserialize(using = HostAndPortDeserializer.class)
		public HostAndPort bindAddress = HostAndPort.fromString("0.0.0.0:18090");
	}

	@Valid
	@NotNull
	@JsonProperty("options")
	public OptionsConfig options = new OptionsConfig();

	public static class OptionsConfig {

		@JsonProperty("include_timestamp")
		public boolean includeTimestamp = true;

		@JsonProperty("include_help")
		public boolean includeHelp = true;

		@JsonProperty("include_type")
		public boolean includeType = true;

		@Min(0L)
		@Max(60000L)
		@JsonProperty("minimum_response_time")
		public long minimumResponseTime = 0L;
	}

	@NotNull
	@JsonProperty("rules")
	public List<@Valid @NotNull PrometheusScrapeRule> rules = new ArrayList<>();

	public static class PrometheusScrapeRule implements ScrapeRule {

		@JsonProperty("pattern")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		@JsonDeserialize(contentUsing = AttributeNamePatternDeserializer.class)
		public List<AttributeNamePattern> patterns;

		@JsonProperty("skip")
		public boolean skip = false;

		@JsonProperty("transform")
		@JsonDeserialize(using = ScriptDeserializer.class)
		public Script<?> transform;

		@Override
		public boolean skip() {
			return skip;
		}

		@Override
		public List<AttributeNamePattern> patterns() {
			return patterns;
		}
	}

	@JsonProperty("labels")
	@JsonDeserialize(using = LabelsDeserializer.class)
	public JsonQuery labels;
}
