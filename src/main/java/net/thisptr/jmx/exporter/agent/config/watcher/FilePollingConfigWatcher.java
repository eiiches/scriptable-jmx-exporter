package net.thisptr.jmx.exporter.agent.config.watcher;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.io.Files;

public class FilePollingConfigWatcher extends PollingConfigWatcher {
	private final File file;

	public FilePollingConfigWatcher(final File file, final ConfigListener listener) throws JsonParseException, JsonMappingException, IOException {
		super(listener);
		this.file = file;
		runOnce();
	}

	@Override
	protected byte[] doLoadConfig() throws IOException {
		return Files.toByteArray(file);
	}

	@Override
	protected String doGetPath() {
		return file.getAbsolutePath();
	}
}
