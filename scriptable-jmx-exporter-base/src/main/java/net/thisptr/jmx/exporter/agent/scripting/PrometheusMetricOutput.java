package net.thisptr.jmx.exporter.agent.scripting;

public interface PrometheusMetricOutput {

	void emit(PrometheusMetric metric);
}