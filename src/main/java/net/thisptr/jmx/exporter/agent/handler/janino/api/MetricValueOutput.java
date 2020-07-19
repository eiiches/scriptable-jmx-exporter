package net.thisptr.jmx.exporter.agent.handler.janino.api;

public interface MetricValueOutput {
	void emit(final MetricValue sample);
}