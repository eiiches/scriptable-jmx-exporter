package net.thisptr.jmx.exporter.agent.config.watcher.loaders;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import net.thisptr.jmx.exporter.agent.config.watcher.PollingConfigWatcher.ConfigLoader;

public class ClassPathConfigLoader implements ConfigLoader {
	private final String path;

	public ClassPathConfigLoader(final String path) {
		this.path = path;
	}

	@Override
	public byte[] bytes() throws IOException {
		try (InputStream is = ClassPathConfigLoader.class.getClassLoader().getResourceAsStream(path)) {
			return ByteStreams.toByteArray(is);
		}
	}

	@Override
	public String toString() {
		return "classpath:" + path;
	}
}
