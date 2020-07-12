package net.thisptr.java.prometheus.metrics.agent;

public interface PrometheusMetricOutput {

	void emit(PrometheusMetric metric);
}