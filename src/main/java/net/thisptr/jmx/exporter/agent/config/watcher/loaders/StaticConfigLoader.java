package net.thisptr.jmx.exporter.agent.config.watcher.loaders;

import java.nio.charset.StandardCharsets;

import net.thisptr.jmx.exporter.agent.config.watcher.PollingConfigWatcher.ConfigLoader;

public class StaticConfigLoader implements ConfigLoader {
	private byte[] bytes;

	public StaticConfigLoader(final byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public byte[] bytes() {
		return bytes;
	}

	@Override
	public String toString() {
		return new String(bytes, StandardCharsets.UTF_8);
	}
}
