package net.thisptr.jmx.exporter.agent.scripting.janino.api;

public interface MetricValueOutput {
	void emit(final MetricValue sample);
}