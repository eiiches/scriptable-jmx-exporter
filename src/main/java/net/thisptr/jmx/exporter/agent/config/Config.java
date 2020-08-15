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
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.net.HostAndPort;

import net.thisptr.jmx.exporter.agent.config.validations.ValidScrapeRule;
import net.thisptr.jmx.exporter.agent.jackson.serdes.AttributeNamePatternDeserializer;
import net.thisptr.jmx.exporter.agent.jackson.serdes.HostAndPortDeserializer;
import net.thisptr.jmx.exporter.agent.jackson.serdes.ScriptTextDeserializer;
import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;
import net.thisptr.jmx.exporter.agent.misc.ScriptText;
import net.thisptr.jmx.exporter.agent.scripting.ConditionScript;
import net.thisptr.jmx.exporter.agent.scripting.Declarations;
import net.thisptr.jmx.exporter.agent.scripting.ScriptEngine;
import net.thisptr.jmx.exporter.agent.scripting.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.scripting.TransformScript;

@JsonDeserialize(builder = Config.Builder.class)
public class Config {

	@JsonPOJOBuilder
	public static class Builder {
		private List<ScriptText> declarations = new ArrayList<>();
		private List<RuleSource> ruleSources = new ArrayList<>();
		private OptionsConfig options = new OptionsConfig();
		private ServerConfig server = new ServerConfig();

		@JsonProperty("declarations")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		@JsonDeserialize(contentUsing = ScriptTextDeserializer.class)
		public Builder withDeclarations(final List<ScriptText> declarations) {
			this.declarations = declarations;
			return this;
		}

		public static class RuleSource {
			@JsonProperty("pattern")
			@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
			@JsonDeserialize(contentUsing = AttributeNamePatternDeserializer.class)
			public List<AttributeNamePattern> patterns;

			@JsonProperty("condition")
			@JsonDeserialize(using = ScriptTextDeserializer.class)
			public ScriptText condition;

			@JsonProperty("skip")
			public boolean skip = false;

			@JsonProperty("transform")
			@JsonDeserialize(using = ScriptTextDeserializer.class)
			public ScriptText transform;
		}

		@JsonProperty("rules")
		public Builder withRules(final List<RuleSource> rules) {
			this.ruleSources = rules;
			return this;
		}

		@JsonProperty("options")
		public Builder withOptions(final OptionsConfig options) {
			this.options = options;
			return this;
		}

		@JsonProperty("server")
		public Builder withServer(final ServerConfig server) {
			this.server = server;
			return this;
		}

		private static final String DEFAULT_ENGINE_NAME = "java";

		public Config build() throws Exception {
			final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();

			final List<Declarations> declarations = new ArrayList<>();
			for (int i = 0; i < this.declarations.size(); i++) {
				final ScriptText script = this.declarations.get(i);
				final ScriptEngine scriptEngine = registry.get(script.engineName != null ? script.engineName : DEFAULT_ENGINE_NAME);
				declarations.add(scriptEngine.compileDeclarations(script.scriptBody, i));
			}

			final List<ScrapeRule> rules = new ArrayList<>();
			for (int i = 0; i < ruleSources.size(); i++) {
				final RuleSource ruleSource = ruleSources.get(i);
				final ScrapeRule rule = new ScrapeRule();
				if (ruleSource.condition != null) {
					final ScriptEngine scriptEngine = registry.get(ruleSource.condition.engineName != null ? ruleSource.condition.engineName : DEFAULT_ENGINE_NAME);
					rule.condition = scriptEngine.compileConditionScript(declarations, ruleSource.condition.scriptBody, i);
				}
				if (ruleSource.transform != null) {
					final ScriptEngine scriptEngine = registry.get(ruleSource.transform.engineName != null ? ruleSource.transform.engineName : DEFAULT_ENGINE_NAME);
					rule.transform = scriptEngine.compileTransformScript(declarations, ruleSource.transform.scriptBody, i);
				}
				rule.skip = ruleSource.skip;
				rule.patterns = ruleSource.patterns;
				rules.add(rule);
			}

			final Config config = new Config();
			config.server = server;
			config.options = options;
			config.declarations = declarations;
			config.rules = rules;
			return config;
		}
	}

	@Valid
	@NotNull
	@JsonProperty("server")
	public ServerConfig server = new ServerConfig();

	public static class ServerConfig {

		@NotNull
		@JsonProperty("bind_address")
		@JsonDeserialize(using = HostAndPortDeserializer.class)
		public HostAndPort bindAddress = HostAndPort.fromString("0.0.0.0:9639");
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
	@JsonProperty("declarations")
	public List<@NotNull Declarations> declarations = new ArrayList<>();

	@NotNull
	@JsonProperty("rules")
	public List<@Valid @ValidScrapeRule @NotNull ScrapeRule> rules = new ArrayList<>();

	public static class ScrapeRule {

		@JsonProperty("pattern")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		@JsonDeserialize(contentUsing = AttributeNamePatternDeserializer.class)
		public List<@NotNull AttributeNamePattern> patterns;

		@JsonProperty("condition")
		public ConditionScript condition;

		@JsonProperty("skip")
		public boolean skip = false;

		@JsonProperty("transform")
		public TransformScript transform;
	}
}
