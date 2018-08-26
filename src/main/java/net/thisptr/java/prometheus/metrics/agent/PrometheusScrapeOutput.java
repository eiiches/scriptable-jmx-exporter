package net.thisptr.java.prometheus.metrics.agent;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.java.prometheus.metrics.agent.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.ScrapeOutput;

public class PrometheusScrapeOutput implements ScrapeOutput<PrometheusScrapeRule> {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Logger LOG = Logger.getLogger(PrometheusScrapeOutput.class.getName());

	public static final JsonQuery DEFAULT_TRANSFORM;
	static {
		try {
			DEFAULT_TRANSFORM = JsonQuery.compile("default_transform_v1");
		} catch (final JsonQueryException e) {
			throw new RuntimeException(e);
		}
	}

	private final Scope scope;
	private final Consumer<PrometheusMetric> output;
	private final Consumer<JsonNode> debugOutput;

	public PrometheusScrapeOutput(final Scope scope, final Consumer<PrometheusMetric> output) {
		this(scope, output, (raw) -> {});
	}

	public PrometheusScrapeOutput(final Scope scope, final Consumer<PrometheusMetric> output, final Consumer<JsonNode> debugOutput) {
		this.scope = scope;
		this.output = output;
		this.debugOutput = debugOutput;
	}

	@Override
	public void emit(final PrometheusScrapeRule rule, final JsonNode mbeanAttributeNode) {
		final Scope childScope = Scope.newChildScope(scope);
		childScope.setValue("type", mbeanAttributeNode.get("type"));
		childScope.setValue("value", mbeanAttributeNode.get("value"));
		childScope.setValue("domain", mbeanAttributeNode.get("domain"));
		childScope.setValue("properties", mbeanAttributeNode.get("properties"));
		childScope.setValue("attribute", mbeanAttributeNode.get("attribute"));
		final JsonQuery transform = rule != null && rule.transform != null ? rule.transform : DEFAULT_TRANSFORM;

		final List<JsonNode> metricNodes;
		try {
			metricNodes = transform.apply(childScope, mbeanAttributeNode);
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
				metric = MAPPER.treeToValue(metricNode, PrometheusMetric.class);
			} catch (final Throwable th) {
				LOG.log(Level.INFO, "Failed to map a Prometheus metric JSON (" + metricNode + ") to an object.", th);
				continue;
			}

			output.accept(metric);
		}
	}
}
