package net.thisptr.jmx.exporter.agent.config.watcher.loaders;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import net.thisptr.jmx.exporter.agent.config.watcher.PollingConfigWatcher.ConfigLoader;

public class FileConfigLoader implements ConfigLoader {
	private final File file;

	public FileConfigLoader(final File file) {
		this.file = file;
	}

	@Override
	public byte[] bytes() throws IOException {
		return Files.toByteArray(file);
	}

	@Override
	public String toString() {
		return file.getAbsolutePath();
	}
}
