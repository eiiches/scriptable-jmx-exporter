package net.thisptr.jmx.exporter.agent.scripting;

import net.thisptr.jmx.exporter.agent.scraper.Sample;

public interface TransformScript {
	void execute(final Sample sample, final PrometheusMetricOutput output);
}
