package net.thisptr.jmx.exporter.agent.handler;

public interface PrometheusMetricOutput {

	void emit(PrometheusMetric metric);
}