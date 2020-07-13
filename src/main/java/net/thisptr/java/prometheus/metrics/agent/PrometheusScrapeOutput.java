package net.thisptr.java.prometheus.metrics.agent;

import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.ScrapeOutput;

public class PrometheusScrapeOutput implements ScrapeOutput<PrometheusScrapeRule> {
	private final PrometheusMetricOutput output;

	public PrometheusScrapeOutput(final PrometheusMetricOutput output) {
		this.output = output;
	}

	@Override
	public void emit(final Sample<PrometheusScrapeRule> sample) {
		sample.rule.transform.execute(sample, output);
	}
}
