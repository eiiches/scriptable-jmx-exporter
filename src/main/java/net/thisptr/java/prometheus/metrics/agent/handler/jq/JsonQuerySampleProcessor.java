package net.thisptr.java.prometheus.metrics.agent.handler.jq;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.java.prometheus.metrics.agent.PrometheusMetric;
import net.thisptr.java.prometheus.metrics.agent.PrometheusMetricOutput;
import net.thisptr.java.prometheus.metrics.agent.RootScope;
import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.handler.SampleProcessor;
import net.thisptr.java.prometheus.metrics.agent.handler.Script;

public class JsonQuerySampleProcessor implements SampleProcessor<JsonQuery> {
	private static final Logger LOG = Logger.getLogger(JsonQuerySampleProcessor.class.getName());

	public static final JsonQuery DEFAULT_TRANSFORM;
	static {
		try {
			DEFAULT_TRANSFORM = JsonQuery.compile("default_transform_v1", Versions.JQ_1_6);
		} catch (final JsonQueryException e) {
			throw new RuntimeException(e);
		}
	}

	private final Scope scope;

	public JsonQuerySampleProcessor() {
		this.scope = RootScope.getInstance();
	}

	@Override
	public Script<JsonQuery> compile(final String script) throws ScriptCompileException {
		try {
			return new Script<>(this, JsonQuery.compile(script, Versions.JQ_1_6));
		} catch (JsonQueryException e) {
			throw new ScriptCompileException(e);
		}
	}

	@Override
	public void handle(final Sample<PrometheusScrapeRule> sample, final JsonQuery script, final PrometheusMetricOutput output) {
		final JsonNode mbeanAttributeNode = SampleToJsonInputConverter.getInstance().convert(sample);
		final JsonQuery transform = sample.rule != null && sample.rule.transform != null ? script : DEFAULT_TRANSFORM;

		final List<JsonNode> metricNodes = new ArrayList<>();
		try {
			transform.apply(scope, mbeanAttributeNode, metricNodes::add);
		} catch (final Throwable th) {
			LOG.log(Level.INFO, "Failed to transform a MBean attribute (" + mbeanAttributeNode + ") to Prometheus metrics.", th);
			return;
		}

		for (final JsonNode metricNode : metricNodes) {
			try {
				// debugOutput.accept(metricNode);
			} catch (final Throwable th) {
				LOG.log(Level.FINEST, "Swallowed an exception ocurred during a debug output.", th);
			}

			final PrometheusMetric metric;
			try {
				metric = JsonOutputToMetricConverter.getInstance().convert(metricNode);
			} catch (final Throwable th) {
				LOG.log(Level.INFO, "Failed to map a Prometheus metric JSON (" + metricNode + ") to an object.", th);
				continue;
			}

			metric.timestamp = sample.timestamp;

			output.emit(metric);
		}
	}

}
