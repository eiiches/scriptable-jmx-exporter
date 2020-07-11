package net.thisptr.java.prometheus.metrics.agent.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.thisptr.java.prometheus.metrics.agent.utils.MoreValidators;

public abstract class PollingConfigWatcher extends Thread implements ConfigWatcher {
	private static final Logger LOG = Logger.getLogger(PollingConfigWatcher.class.getName());
	private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

	private final ConfigWatcher.ConfigListener listener;

	private volatile Config config;
	private volatile byte[] bytes;

	protected abstract String doGetPath();

	protected abstract byte[] doLoadConfig() throws IOException;

	public PollingConfigWatcher(final ConfigWatcher.ConfigListener listener) throws JsonParseException, JsonMappingException, IOException {
		this.listener = listener;
		this.setName("Prometheus Metrics Agent " + PollingConfigWatcher.class.getSimpleName());
		this.setDaemon(true);
	}
	
	protected void runOnce() throws IOException {
		this.bytes = doLoadConfig();
		this.config = YAML_MAPPER.readValue(this.bytes, Config.class);
		MoreValidators.validate(this.config);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException ie) {
				LOG.log(Level.INFO, "Interrupted. Stopping Prometheus Metrics Agent " + PollingConfigWatcher.class.getSimpleName() + "...");
				break;
			}

			final byte[] newBytes;
			try {
				newBytes = doLoadConfig();
				if (Arrays.equals(bytes, newBytes))
					continue;
			} catch (final Throwable th) {
				LOG.log(Level.WARNING, "Got exception while reloading " + doGetPath() + ".", th);
				continue;
			}

			final Config newConfig;
			try {
				newConfig = YAML_MAPPER.readValue(newBytes, Config.class);
				MoreValidators.validate(newConfig);
			} catch (final Throwable th) {
				LOG.log(Level.WARNING, "Got exception while reloading " + doGetPath() + ".", th);
				continue;
			}

			try {
				listener.changed(config, newConfig);
			} catch (final Throwable th) {
				LOG.log(Level.WARNING, "Got exception while reconfiguring from " + doGetPath() + ".", th);
			}

			this.config = newConfig;
			this.bytes = newBytes;
		}
	}

	public Config config() {
		return config;
	}

	public void shutdown() {
		this.interrupt();
	}
}