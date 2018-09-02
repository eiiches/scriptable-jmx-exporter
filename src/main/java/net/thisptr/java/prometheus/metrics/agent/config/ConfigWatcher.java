package net.thisptr.java.prometheus.metrics.agent.config;

public interface ConfigWatcher {
	Config config();

	void start();

	interface ConfigListener {
		void changed(Config oldConfig, Config newConfig) throws Throwable;
	}

	void shutdown();
}