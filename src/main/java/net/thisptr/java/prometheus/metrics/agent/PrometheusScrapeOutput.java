package net.thisptr.java.prometheus.metrics.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.ScrapeOutput;

public class PrometheusScrapeOutput implements ScrapeOutput<PrometheusScrapeRule> {
	private static final Logger LOG = Logger.getLogger(PrometheusScrapeOutput.class.getName());

	public static final JsonQuery DEFAULT_TRANSFORM;
	static {
		try {
			DEFAULT_TRANSFORM = JsonQuery.compile("default_transform_v1", Versions.JQ_1_6);
		} catch (final JsonQueryException e) {
			throw new RuntimeException(e);
		}
	}

	private final Scope scope;
	private final PrometheusMetricOutput output;
	private final Consumer<JsonNode> debugOutput;

	public interface PrometheusMetricOutput {

		void emit(PrometheusMetric metric);
	}

	public PrometheusScrapeOutput(final Scope scope, final PrometheusMetricOutput output) {
		this(scope, output, (raw) -> {});
	}

	public PrometheusScrapeOutput(final Scope scope, final PrometheusMetricOutput output, final Consumer<JsonNode> debugOutput) {
		this.scope = scope;
		this.output = output;
		this.debugOutput = debugOutput;
	}

	@Override
	public void emit(final Sample<PrometheusScrapeRule> sample) {
		final JsonNode mbeanAttributeNode = sample.toJsonNode();
		final JsonQuery transform = sample.rule != null && sample.rule.transform != null ? sample.rule.transform : DEFAULT_TRANSFORM;

		final List<JsonNode> metricNodes = new ArrayList<>();
		try {
			transform.apply(scope, mbeanAttributeNode, metricNodes::add);
		} catch (final Throwable th) {
			LOG.log(Level.INFO, "Failed to transform a MBean attribute (" + mbeanAttributeNode + ") to Prometheus metrics.", th);
			return;
		}

		for (final JsonNode metricNode : metricNodes) {
			try {
				debugOutput.accept(metricNode);
			} catch (final Throwable th) {
				LOG.log(Level.FINEST, "Swallowed an exception ocurred during a debug output.", th);
			}

			final PrometheusMetric metric;
			try {
				metric = PrometheusMetric.fromJsonNode(metricNode);
			} catch (final Throwable th) {
				LOG.log(Level.INFO, "Failed to map a Prometheus metric JSON (" + metricNode + ") to an object.", th);
				continue;
			}

			if (metric.timestamp == null) {
				metric.timestamp = sample.timestamp;
			}

			output.emit(metric);
		}
	}
}
