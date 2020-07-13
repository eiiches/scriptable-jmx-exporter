package net.thisptr.java.prometheus.metrics.agent.handler;

import net.thisptr.java.prometheus.metrics.agent.PrometheusMetricOutput;
import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;

public class Script<T> {
	private final ScriptEngine<T> engine;
	private final T script;

	public Script(final ScriptEngine<T> engine, final T script) {
		this.engine = engine;
		this.script = script;
	}

	public void execute(final Sample<PrometheusScrapeRule> sample, final PrometheusMetricOutput output) {
		engine.handle(sample, script, output);
	}
}
