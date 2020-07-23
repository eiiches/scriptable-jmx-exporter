package net.thisptr.jmx.exporter.agent.handler;

import net.thisptr.jmx.exporter.agent.PrometheusMetricOutput;
import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;

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
