package net.thisptr.jmx.exporter.agent.handler.jq;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jmx.exporter.agent.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.PrometheusMetricOutput;
import net.thisptr.jmx.exporter.agent.RootScope;
import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.jmx.exporter.agent.handler.ConditionScript;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngine;
import net.thisptr.jmx.exporter.agent.handler.TransformScript;

public class JsonQueryScriptEngine implements ScriptEngine {
	private static final Logger LOG = Logger.getLogger(JsonQueryScriptEngine.class.getName());

	private static class TransformScriptImpl implements TransformScript {
		private final JsonQuery query;
		private final Scope scope;

		public TransformScriptImpl(final JsonQuery query) {
			this.query = query;
			this.scope = RootScope.getInstance();
		}

		@Override
		public void execute(final Sample<PrometheusScrapeRule> sample, final PrometheusMetricOutput output) {
			final JsonNode mbeanAttributeNode = SampleToJsonInputConverter.getInstance().convert(sample);

			final List<JsonNode> metricNodes = new ArrayList<>();
			try {
				query.apply(scope, mbeanAttributeNode, metricNodes::add);
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

	@Override
	public TransformScript compileTransformScript(final String script) throws ScriptCompileException {
		try {
			return new TransformScriptImpl(JsonQuery.compile(script, Versions.JQ_1_6));
		} catch (JsonQueryException e) {
			throw new ScriptCompileException(e);
		}
	}

	@Override
	public ConditionScript compileConditionScript(final String script) throws ScriptCompileException {
		throw new UnsupportedOperationException("!jq engine does not support condition scripts");
	}
}
