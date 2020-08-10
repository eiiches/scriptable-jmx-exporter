package net.thisptr.jmx.exporter.agent.handler;

import net.thisptr.jmx.exporter.agent.PrometheusMetricOutput;
import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;

public interface TransformScript {
	void execute(final Sample<PrometheusScrapeRule> sample, final PrometheusMetricOutput output);
}
