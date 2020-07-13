package net.thisptr.java.prometheus.metrics.agent.handler.janino.iface;

public interface MetricValueOutput {
	void emit(final MetricValue sample);
}