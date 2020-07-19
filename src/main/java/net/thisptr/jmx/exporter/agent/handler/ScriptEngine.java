package net.thisptr.jmx.exporter.agent.handler;

import net.thisptr.jmx.exporter.agent.PrometheusMetricOutput;
import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;

public interface ScriptEngine<T> {

	public static class ScriptCompileException extends Exception {
		private static final long serialVersionUID = 1L;

		public ScriptCompileException(String message, Throwable cause) {
			super(message, cause);
		}

		public ScriptCompileException(Throwable cause) {
			super(cause);
		}
	}

	Script<T> compile(String script) throws ScriptCompileException;

	void handle(Sample<PrometheusScrapeRule> sample, T script, PrometheusMetricOutput output);
}
