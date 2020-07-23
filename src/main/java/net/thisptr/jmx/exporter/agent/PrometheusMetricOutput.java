package net.thisptr.jmx.exporter.agent;

public interface PrometheusMetricOutput {

	void emit(PrometheusMetric metric);
}