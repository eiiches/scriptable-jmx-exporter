package net.thisptr.java.prometheus.metrics.agent;

import java.util.logging.Logger;

import net.thisptr.jackson.jq.Scope;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.ScrapeOutput;

public class PrometheusScrapeOutput implements ScrapeOutput<PrometheusScrapeRule> {
	private static final Logger LOG = Logger.getLogger(PrometheusScrapeOutput.class.getName());

	private final Scope scope;
	private final PrometheusMetricOutput output;

	public PrometheusScrapeOutput(final Scope scope, final PrometheusMetricOutput output) {
		this.scope = scope;
		this.output = output;
	}

	@Override
	public void emit(final Sample<PrometheusScrapeRule> sample) {
		sample.rule.transform.execute(sample, output);
	}
}
