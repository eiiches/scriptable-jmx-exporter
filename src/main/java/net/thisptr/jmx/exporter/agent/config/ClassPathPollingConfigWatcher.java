package net.thisptr.jmx.exporter.agent.config;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.io.ByteStreams;

public class ClassPathPollingConfigWatcher extends PollingConfigWatcher {
	private final String path;

	public ClassPathPollingConfigWatcher(final String path, final ConfigListener listener) throws JsonParseException, JsonMappingException, IOException {
		super(listener);
		this.path = path;
		runOnce();
	}

	@Override
	protected byte[] doLoadConfig() throws IOException {
		try (InputStream is = ClassPathPollingConfigWatcher.class.getClassLoader().getResourceAsStream(path)) {
			return ByteStreams.toByteArray(is);
		}
	}

	@Override
	protected String doGetPath() {
		return "classpath:" + path;
	}
}
