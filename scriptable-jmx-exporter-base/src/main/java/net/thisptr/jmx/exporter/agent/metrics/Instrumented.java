package net.thisptr.jmx.exporter.agent.metrics;

import java.util.function.Consumer;

import net.thisptr.jmx.exporter.agent.scripting.PrometheusMetric;

public interface Instrumented {
	void toPrometheus(Consumer<PrometheusMetric> fn);
}
