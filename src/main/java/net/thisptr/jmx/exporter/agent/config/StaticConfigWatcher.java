package net.thisptr.jmx.exporter.agent.config;

public class StaticConfigWatcher implements ConfigWatcher {
	private final Config config;

	public StaticConfigWatcher(final Config config) {
		this.config = config;
	}

	public Config config() {
		return config;
	}

	@Override
	public void start() {}

	@Override
	public void shutdown() {}
}