package net.thisptr.java.prometheus.metrics.agent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xnio.Options;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.net.HostAndPort;

import io.undertow.Undertow;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import net.thisptr.java.prometheus.metrics.agent.config.ClassPathPollingConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.config.Config;
import net.thisptr.java.prometheus.metrics.agent.config.ConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.config.ConfigWatcher.ConfigListener;
import net.thisptr.java.prometheus.metrics.agent.config.FilePollingConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.config.StaticConfigWatcher;
import net.thisptr.java.prometheus.metrics.agent.handler.ScriptEngineRegistry;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.JaninoScriptEngine;
import net.thisptr.java.prometheus.metrics.agent.handler.jq.JsonQueryScriptEngine;
import net.thisptr.java.prometheus.metrics.agent.utils.MoreValidators;

public class Agent {
	private static final Logger LOG = Logger.getLogger(Agent.class.getName());
	private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

	static final String DEFAULT_CLASSPATH_CONFIG_FILE = "java-prometheus-metrics-agent-default.yaml";

	private static Undertow SERVER;
	private static volatile PrometheusExporterHttpHandler HANDLER;

	static {
		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("jq", new JsonQueryScriptEngine());
		registry.add("java", new JaninoScriptEngine());
		registry.setDefault("jq");
	}

	private static ConfigWatcher newConfigWatcher(String args, final ConfigListener listener) throws JsonParseException, JsonMappingException, IOException {
		if (args == null)
			args = "@classpath:" + DEFAULT_CLASSPATH_CONFIG_FILE;
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
	private static void safeStart(final Undertow server) throws Throwable {
		try {
			server.start();
		} catch (final Throwable th) {
			try {
				server.stop(); // closes a server socket.
			} catch (final Throwable th2) {
				th.addSuppressed(th2);
			}
			throw th;
		}
	}

	private static Undertow newServer(final HostAndPort hostAndPort) {
		final HttpHandler thisHandler = exchange -> HANDLER.handleRequest(exchange);
		final EncodingHandler encodingHandler = new EncodingHandler(new ContentEncodingRepository()
				.addEncodingHandler("gzip",
						new GzipEncodingProvider(), 50,
						Predicates.parse("max-content-size(5)")))
								.setNext(thisHandler);
		return Undertow.builder()
				.setWorkerOption(Options.WORKER_NAME, "MBeanExporterIO")
				.addHttpListener(hostAndPort.getPort(), hostAndPort.getHost())
				.setHandler(encodingHandler)
				.build();
	}

	public static void premain(final String args) throws Throwable {
		LOG.log(Level.INFO, "Starting Prometheus Metrics Agent...");
		try {
			final ConfigWatcher watcher = newConfigWatcher(args, (oldConfig, newConfig) -> {
				LOG.log(Level.FINE, "Detected configuration change. Reconfiguring Prometheus Metrics Agent...");
				final PrometheusExporterHttpHandler handler = new PrometheusExporterHttpHandler(newConfig.rules, newConfig.labels, newConfig.options);
				if (!oldConfig.server.bindAddress.equals(newConfig.server.bindAddress)) {
					try {
						SERVER.stop();
					} catch (final Throwable th) {
						LOG.log(Level.WARNING, "Failed to stop Prometheus Metrics Agent server for reconfiguration.", th);
					}
					SERVER = newServer(newConfig.server.bindAddress);
					safeStart(SERVER);
				}
				HANDLER = handler;
				LOG.log(Level.INFO, "Successfully reconfigured Prometheus Metrics Agent.");
			});

			final Config initialConfig = watcher.config();
			HANDLER = new PrometheusExporterHttpHandler(initialConfig.rules, initialConfig.labels, initialConfig.options);
			SERVER = newServer(initialConfig.server.bindAddress);
			safeStart(SERVER);
			watcher.start();
		} catch (final Throwable th) {
			LOG.log(Level.SEVERE, "Failed to start Prometheus Metrics Agent.", th);
			throw th;
		}
	}
}
