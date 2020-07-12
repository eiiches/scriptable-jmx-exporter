package net.thisptr.java.prometheus.metrics.agent.handler;

import net.thisptr.java.prometheus.metrics.agent.PrometheusMetricOutput;
import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;

public class Script<T> {
	private final SampleProcessor<T> processor;
	private final T script;

	public Script(final SampleProcessor<T> processor, final T script) {
		this.processor = processor;
		this.script = script;
	}

	public void execute(final Sample<PrometheusScrapeRule> sample, final PrometheusMetricOutput output) {
		processor.handle(sample, script, output);
	}
}
