package net.thisptr.jmx.exporter.agent.registry;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class Registry {
	private static PrometheusMeterRegistry INSTANCE = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

	// NOTE: this method is invoked from ScriptContext using reflection, so don't change the name
	public static PrometheusMeterRegistry getInstance() {
		return INSTANCE;
	}
}
