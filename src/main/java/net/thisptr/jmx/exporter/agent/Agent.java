package net.thisptr.jmx.exporter.agent;

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
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import net.thisptr.jmx.exporter.agent.config.ClassPathPollingConfigWatcher;
import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.config.ConfigWatcher;
import net.thisptr.jmx.exporter.agent.config.ConfigWatcher.ConfigListener;
import net.thisptr.jmx.exporter.agent.config.FilePollingConfigWatcher;
import net.thisptr.jmx.exporter.agent.config.StaticConfigWatcher;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.handler.janino.JaninoScriptEngine;
import net.thisptr.jmx.exporter.agent.utils.MoreValidators;

public class Agent {
	private static final Logger LOG = Logger.getLogger(Agent.class.getName());
	private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

	static final String DEFAULT_CLASSPATH_CONFIG_FILE = "scriptable-jmx-exporter.yaml";

	private static Undertow SERVER;
	private static volatile PrometheusExporterHttpHandler HANDLER;

	static {
		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("java", new JaninoScriptEngine());
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
				.addEncodingHandler("gzip", new GzipEncodingProvider(), 50))
						.setNext(thisHandler);
		return Undertow.builder()
				.setWorkerOption(Options.WORKER_NAME, "scriptable-jmx-exporter")
				.setWorkerOption(Options.THREAD_DAEMON, true)
				.addHttpListener(hostAndPort.getPort(), hostAndPort.getHost())
				.setHandler(encodingHandler)
				.build();
	}

	public static void premain(final String args) throws Throwable {
		LOG.log(Level.INFO, "Starting Scriptable JMX Exporter...");
		try {
			final ConfigWatcher watcher = newConfigWatcher(args, (oldConfig, newConfig) -> {
				LOG.log(Level.FINE, "Detected configuration change. Reconfiguring Scriptable JMX Exporter...");
				final PrometheusExporterHttpHandler handler = new PrometheusExporterHttpHandler(newConfig.rules, newConfig.options);
				if (!oldConfig.server.bindAddress.equals(newConfig.server.bindAddress)) {
					try {
						SERVER.stop();
					} catch (final Throwable th) {
						LOG.log(Level.WARNING, "Failed to stop Scriptable JMX Exporter server for reconfiguration.", th);
					}
					SERVER = newServer(newConfig.server.bindAddress);
					safeStart(SERVER);
				}
				HANDLER = handler;
				LOG.log(Level.INFO, "Successfully reconfigured Scriptable JMX Exporter.");
			});

			final Config initialConfig = watcher.config();
			HANDLER = new PrometheusExporterHttpHandler(initialConfig.rules, initialConfig.options);
			SERVER = newServer(initialConfig.server.bindAddress);
			safeStart(SERVER);
			watcher.start();
		} catch (final Throwable th) {
			LOG.log(Level.SEVERE, "Failed to start Scriptable JMX Exporter.", th);
			System.exit(1);
		}
	}
}
