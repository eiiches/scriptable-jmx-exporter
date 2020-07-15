package net.thisptr.java.prometheus.metrics.agent.handler.janino.api;

public interface MetricValueOutput {
	void emit(final MetricValue sample);
}