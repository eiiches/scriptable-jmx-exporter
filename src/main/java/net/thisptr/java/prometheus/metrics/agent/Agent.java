package net.thisptr.java.prometheus.metrics.agent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import fi.iki.elonen.NanoHTTPD;
import net.thisptr.java.prometheus.metrics.agent.config.ClassPathPollingConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.config.Config;
import net.thisptr.java.prometheus.metrics.agent.config.ConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.config.ConfigWatcher.ConfigListener;
import net.thisptr.java.prometheus.metrics.agent.handler.SampleProcessorRegistry;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.JaninoSampleProcessor;
import net.thisptr.java.prometheus.metrics.agent.handler.jq.JsonQuerySampleProcessor;
import net.thisptr.java.prometheus.metrics.agent.config.FilePollingConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.config.StaticConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.utils.MoreValidators;

public class Agent {
	private static final Logger LOG = Logger.getLogger(Agent.class.getName());
	private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

	private static PrometheusExporterServer SERVER;

	static {
		final SampleProcessorRegistry registry = SampleProcessorRegistry.getInstance();
		registry.add("jq", new JsonQuerySampleProcessor());
		registry.add("java", new JaninoSampleProcessor());
		registry.setDefault("jq");
	}

	private static ConfigWatcher newConfigWatcher(String args, final ConfigListener listener) throws JsonParseException, JsonMappingException, IOException {
		if (args == null)
			args = "@classpath:java-prometheus-metrics-agent-default.yaml";
		if (args.isEmpty()) {
			return new StaticConfigWatcher(new Config());
		} else if (args.startsWith("@classpath:")) {
			return new ClassPathPollingConfigWatcher(args.substring("@classpath:".length()), listener);
		} else if (args.startsWith("@")) {
			return new FilePollingConfigWatcher(new File(args.substring(1)), listener);
		} else {
			final Config config = MAPPER.readValue(args, Config.class);
			MoreValidators.validate(config);
			return new StaticConfigWatcher(config);
		}
	}

	/**
	 * To avoid leaking a {@link java.net.ServerSocket} instance.
	 *
	 * @param server
	 * @param b
	 * @param timeout
	 * @throws Throwable
	 */
	private static void safeStart(final NanoHTTPD server, final int timeout, final boolean daemon) throws Throwable {
		try {
			server.start(timeout, daemon);
		} catch (final Throwable th) {
			try {
				server.stop(); // closes a server socket.
			} catch (final Throwable th2) {
				th.addSuppressed(th2);
			}
			throw th;
		}
	}

	public static void premain(final String args) throws Throwable {
		LOG.log(Level.INFO, "Starting Prometheus Metrics Agent...");
		try {
			final ConfigWatcher watcher = newConfigWatcher(args, (oldConfig, newConfig) -> {
				LOG.log(Level.FINE, "Detected configuration change. Reconfiguring Prometheus Metrics Agent...");
				final PrometheusExporterServerHandler handler = new PrometheusExporterServerHandler(newConfig.rules, newConfig.labels, newConfig.options);
				if (!oldConfig.server.bindAddress.equals(newConfig.server.bindAddress)) {
					try {
						SERVER.stop();
					} catch (final Throwable th) {
						LOG.log(Level.WARNING, "Failed to stop Prometheus Metrics Agent server for reconfiguration.", th);
					}
					SERVER = new PrometheusExporterServer(newConfig.server.bindAddress, handler);
					safeStart(SERVER, NanoHTTPD.SOCKET_READ_TIMEOUT, true);
				} else {
					SERVER.configure(handler);
				}
				LOG.log(Level.INFO, "Successfully reconfigured Prometheus Metrics Agent.");
			});

			final Config initialConfig = watcher.config();
			final PrometheusExporterServerHandler handler = new PrometheusExporterServerHandler(initialConfig.rules, initialConfig.labels, initialConfig.options);
			SERVER = new PrometheusExporterServer(initialConfig.server.bindAddress, handler);
			safeStart(SERVER, NanoHTTPD.SOCKET_READ_TIMEOUT, true);
			watcher.start();
		} catch (final Throwable th) {
			LOG.log(Level.SEVERE, "Failed to start Prometheus Metrics Agent.", th);
			throw th;
		}
	}
}
