package net.thisptr.jmx.exporter.agent.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.net.HostAndPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import net.thisptr.jmx.exporter.agent.config.deserializers.AttributeNamePatternDeserializer;
import net.thisptr.jmx.exporter.agent.config.deserializers.HostAndPortDeserializer;
import net.thisptr.jmx.exporter.agent.config.deserializers.ScriptTextDeserializer;
import net.thisptr.jmx.exporter.agent.config.validations.ValidScrapeRule;
import net.thisptr.jmx.exporter.agent.config.validations.ValidScrapeRuleList;
import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;
import net.thisptr.jmx.exporter.agent.misc.Pair;
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

	public static Config createDefault() {
		final Config config = new Config();
		config.server.bindAddress = HostAndPort.fromString("0.0.0.0:9639");
		config.options.includeHelp = true;
		config.options.includeTimestamp = true;
		config.options.includeType = true;
		config.options.minimumResponseTime = 0L;
		final ScrapeRule rule = new ScrapeRule();
		try {
			rule.transform = ScriptEngineRegistry.getInstance().get("java").compileTransformScript(Collections.emptyList(), "V1.transform(in, out, \"type\");", -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		rule.patterns = Collections.emptyList();
		config.rules.add(rule);
		return config;
	}

	@Valid
	@NotNull
	@JsonProperty("server")
	public ServerConfig server = new ServerConfig();

	public static class ServerConfig {

		@NotNull
		@JsonProperty("bind_address")
		@JsonDeserialize(using = HostAndPortDeserializer.class)
		public HostAndPort bindAddress;
	}

	@Valid
	@NotNull
	@JsonProperty("options")
	public OptionsConfig options = new OptionsConfig();

	public static class OptionsConfig {

		@NotNull
		@JsonProperty("include_timestamp")
		public Boolean includeTimestamp;

		@NotNull
		@JsonProperty("include_help")
		public Boolean includeHelp;

		@NotNull
		@JsonProperty("include_type")
		public Boolean includeType;

		@Min(0L)
		@Max(60000L)
		@NotNull
		@JsonProperty("minimum_response_time")
		public Long minimumResponseTime;
	}

	@NotNull
	@JsonProperty("declarations")
	public List<@NotNull Declarations> declarations = new ArrayList<>();

	@NotNull
	@ValidScrapeRuleList
	@JsonProperty("rules")
	public List<@Valid @ValidScrapeRule @NotNull ScrapeRule> rules = new ArrayList<>();

	public static class ScrapeRule {

		@JsonProperty("pattern")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		@JsonDeserialize(contentUsing = AttributeNamePatternDeserializer.class)
		public List<AttributeNamePattern> patterns;

		@JsonProperty("condition")
		public ConditionScript condition;

		@JsonProperty("skip")
		public boolean skip = false;

		@JsonProperty("transform")
		public TransformScript transform;
	}

	public static Config merge(final List<Config> configs) {
		Config config = Config.createDefault();
		for (final Config override : configs)
			config.merge(override);
		return config;
	}

	public void merge(final Config other) {
		if (other.server != null) {
			if (other.server.bindAddress != null)
				this.server.bindAddress = other.server.bindAddress;
		}
		if (other.options != null) {
			if (other.options.includeHelp != null)
				this.options.includeHelp = other.options.includeHelp;
			if (other.options.includeTimestamp != null)
				this.options.includeTimestamp = other.options.includeTimestamp;
			if (other.options.includeType != null)
				this.options.includeType = other.options.includeType;
			if (other.options.minimumResponseTime != null)
				this.options.minimumResponseTime = other.options.minimumResponseTime;
		}
		if (other.declarations != null) {
			// Actually, this is meaningless. Declarations are scoped to a single configuration file.
			// These merged declarations are never used.
			this.declarations.addAll(other.declarations);
		}
		if (other.rules != null && !other.rules.isEmpty()) {
			final Map<Pair<List<AttributeNamePattern>, ConditionScript>, ScrapeRule> mergedRules = new LinkedHashMap<>();
			if (this.rules != null)
				for (final ScrapeRule rule : this.rules)
					mergedRules.put(Pair.of(rule.patterns != null ? rule.patterns : Collections.emptyList(), rule.condition), rule);
			for (final ScrapeRule rule : other.rules)
				mergedRules.put(Pair.of(rule.patterns != null ? rule.patterns : Collections.emptyList(), rule.condition), rule);
			final ScrapeRule defaultRule = mergedRules.remove(Pair.of(Collections.emptyList(), null));
			this.rules = new ArrayList<>(mergedRules.values());
			this.rules.add(defaultRule);
		}
	}
}
