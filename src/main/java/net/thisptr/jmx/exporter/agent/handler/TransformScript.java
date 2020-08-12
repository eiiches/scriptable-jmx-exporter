package net.thisptr.jmx.exporter.agent.handler;

import net.thisptr.jmx.exporter.agent.PrometheusMetricOutput;
import net.thisptr.jmx.exporter.agent.Sample;

public interface TransformScript {
	void execute(final Sample sample, final PrometheusMetricOutput output);
}
