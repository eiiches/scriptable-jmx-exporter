package net.thisptr.jmx.exporter.agent.config.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.config.watcher.loaders.ClassPathConfigLoader;
import net.thisptr.jmx.exporter.agent.config.watcher.loaders.FileConfigLoader;
import net.thisptr.jmx.exporter.agent.config.watcher.loaders.StaticConfigLoader;
import net.thisptr.jmx.exporter.agent.metrics.Instrumented;
import net.thisptr.jmx.exporter.agent.scripting.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.utils.MoreValidators;

public class PollingConfigWatcher extends Thread implements ConfigWatcher, Instrumented {
	private static final Logger LOG = Logger.getLogger(PollingConfigWatcher.class.getName());

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

	private final ConfigWatcher.ConfigListener listener;

	public interface ConfigLoader {
		/**
		 * @return Pair&lt;IsChanged, NewConfig&gt;, where IsChanged represents whether the configuration has changed since the last invocation.
		 * @throws IOException
		 */
		public byte[] bytes() throws IOException;

		public String toString();
	}

	private final List<ConfigLoader> loaders = new ArrayList<>();

	public PollingConfigWatcher(final String args, final ConfigWatcher.ConfigListener listener) {
		this.listener = listener;
		setName("Scriptable JMX Exporter " + PollingConfigWatcher.class.getSimpleName());
		setDaemon(true);
		instanciateConfigLoaders(args.getBytes(StandardCharsets.UTF_8), loaders::add);
		try {
			reload();
			lastSuccess = true;
		} catch (Exception e) {
			lastSuccess = false;
			throw new RuntimeException(e);
		}
	}

	private volatile boolean shutdownRequested = false;
	private volatile Config config;
	private List<byte[]> bytes = Collections.emptyList(); // don't need volatile or locking; only accessed by watcher thread

	private volatile boolean lastSuccess = true;
	private final AtomicInteger reloadTotal = new AtomicInteger();

	/**
	 * @return true if a new config is loaded, false if unchanged
	 * @throws Exception
	 */
	private boolean reload() throws Exception {
		final List<byte[]> newBytes = new ArrayList<>(loaders.size());
		for (int i = 0; i < loaders.size(); ++i) {
			try {
				newBytes.add(loaders.get(i).bytes());
			} catch (final Throwable th) {
				LOG.log(Level.WARNING, "Failed to load configuration from " + loaders.get(i).toString() + ".", th);
				throw th;
			}
		}

		if (bytesEquals(bytes, newBytes))
			return false;

		final List<Config> newConfigs = new ArrayList<>(loaders.size());
		for (int i = 0; i < newBytes.size(); ++i) {
			try {
				newConfigs.add(YAML_MAPPER.readValue(newBytes.get(i), Config.class));
			} catch (final Throwable th) {
				LOG.log(Level.WARNING, "Failed to load configuration from " + loaders.get(i).toString() + ".", th);
				throw th;
			}
		}

		final Config newMergedConfig = Config.merge(newConfigs);
		try {
			MoreValidators.validate(newMergedConfig);
		} catch (final Throwable th) {
			LOG.log(Level.WARNING, "Failed to load configuration. The configuration is invalid.", th);
			throw th;
		}

		this.config = newMergedConfig;
		this.bytes = newBytes;
		return true;
	}

	static void instanciateConfigLoaders(final byte[] buf, final Consumer<ConfigLoader> fn) {
		int offset = 0;
		while (offset < buf.length) {
			if (buf[offset] == '@') {
				final int start = offset + 1; // +1 for @
				while (offset < buf.length && buf[offset] != ',')
					offset++;
				final String path = new String(buf, start, offset - start);
				if (path.startsWith("classpath:")) {
					fn.accept(new ClassPathConfigLoader(path.substring("classpath:".length())));
				} else {
					fn.accept(new FileConfigLoader(new File(path)));
				}
				offset++; // skip comma
			} else {
				try (final JsonParser parser = MAPPER.createParser(buf, offset, buf.length - offset)) {
					final int start = offset;
					MAPPER.readValue(parser, JsonNode.class);
					offset += (int) parser.getCurrentLocation().getByteOffset();
					fn.accept(new StaticConfigLoader(Arrays.copyOfRange(buf, start, offset)));
					offset++; // skip comma
				} catch (IOException e) {
					final String json = new String(buf, offset, buf.length - offset, StandardCharsets.UTF_8);
					throw new RuntimeException("Unable to parse JSON out of the first part of the input: " + json, e);
				}
			}
		}
	}

	private static boolean bytesEquals(final List<byte[]> a, final List<byte[]> b) {
		if (a.size() != b.size())
			return false;
		for (int i = 0; i < a.size(); i++) {
			if (!Arrays.equals(a.get(i), b.get(i)))
				return false;
		}
		return true;
	}

	@Override
	public void run() {
		while (!shutdownRequested) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException ie) {
				LOG.log(Level.INFO, "Interrupted. Stopping Scriptable JMX Exporter " + PollingConfigWatcher.class.getSimpleName() + "...");
				break;
			}

			final Config oldConfig = config;
			final boolean changed;
			try {
				changed = reload();
			} catch (final Throwable th) {
				lastSuccess = false;
				continue; // just continue, we already logged a warning
			}
			if (!changed) {
				lastSuccess = true;
				continue;
			}

			try {
				listener.changed(oldConfig, config);
				reloadTotal.incrementAndGet();
				lastSuccess = true;
			} catch (final Throwable th) {
				lastSuccess = false;
				LOG.log(Level.WARNING, "Got unexpected exception while invoking listeners.", th);
			}
		}
	}

	public void shutdown() {
		shutdownRequested = true;
		interrupt();
	}

	@Override
	public Config config() {
		return config;
	}

	@Override
	public void toPrometheus(final Consumer<PrometheusMetric> fn) {
		final PrometheusMetric m1 = new PrometheusMetric();
		m1.name = "scriptable_jmx_exporter_config_success";
		m1.value = lastSuccess ? 1.0 : 0.0;
		m1.type = "gauge";
		m1.help = "1 if the configurations in use and on disk are the same. 0 indicates the configurations on disk could not be reloaded due to some errors.";
		fn.accept(m1);

		final PrometheusMetric m2 = new PrometheusMetric();
		m2.name = "scriptable_jmx_exporter_config_reload_success";
		m2.value = reloadTotal.get();
		m2.type = "counter";
		m2.suffix = "total";
		m2.help = "The total number of times configurations are reloaded successfully.";
		fn.accept(m2);
	}
}
