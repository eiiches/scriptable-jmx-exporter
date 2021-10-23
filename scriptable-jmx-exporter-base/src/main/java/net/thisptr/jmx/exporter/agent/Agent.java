package net.thisptr.jmx.exporter.agent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr.FlightRecorderModule;
import org.xnio.Options;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.net.HostAndPort;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.config.watcher.ConfigWatcher;
import net.thisptr.jmx.exporter.agent.config.watcher.ConfigWatcher.ConfigListener;
import net.thisptr.jmx.exporter.agent.metrics.Instrumented;
import net.thisptr.jmx.exporter.agent.metrics.MetricRegistry;
import net.thisptr.jmx.exporter.agent.config.watcher.PollingConfigWatcher;
import net.thisptr.jmx.exporter.agent.scripting.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.scripting.janino.JaninoScriptEngine;

public class Agent {
	private static final Logger LOG = Logger.getLogger(Agent.class.getName());

	static final String DEFAULT_CLASSPATH_CONFIG_FILE = "scriptable-jmx-exporter.yaml";

	private static Undertow SERVER;
	private static volatile ExporterHttpHandler HANDLER;
	private static FlightRecorderModule.FlightRecorder RECORDER;

	static {
		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("java", new JaninoScriptEngine());
	}

	private static ConfigWatcher newConfigWatcher(String args, final ConfigListener listener) throws JsonParseException, JsonMappingException, IOException {
		if (args == null)
			args = "@classpath:" + DEFAULT_CLASSPATH_CONFIG_FILE;
		return new PollingConfigWatcher(args, listener);
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
		final BuildInfo buildInfo = BuildInfo.getInstance();
		LOG.log(Level.INFO, "Starting Scriptable JMX Exporter Version {0} (Commit: {1})", new String[] { buildInfo.buildVersion, buildInfo.commitId.substring(0, Math.min(7, buildInfo.commitId.length())) });

		MetricRegistry.getInstance().add(buildInfo);
		try {
			final ConfigWatcher watcher = newConfigWatcher(args, (oldConfig, newConfig) -> {
				LOG.log(Level.FINE, "Detected configuration change. Reconfiguring Scriptable JMX Exporter...");

				final FlightRecorderModule.FlightRecorder recorder = FlightRecorderModule.getInstance().create(newConfig.flightRecorderEventRules);
				recorder.start();

				final ExporterHttpHandler handler = new ExporterHttpHandler(newConfig.rules, newConfig.options, MetricRegistry.getInstance(), newConfig.contexts);
				try {
					if (!oldConfig.server.bindAddress.equals(newConfig.server.bindAddress)) {
						try {
							SERVER.stop();
						} catch (final Throwable th) {
							LOG.log(Level.WARNING, "Failed to stop Scriptable JMX Exporter server for reconfiguration.", th);
						}
						SERVER = newServer(newConfig.server.bindAddress);
						safeStart(SERVER);
					}
				} catch (Throwable th) {
					recorder.close();
					throw th;
				}

				HANDLER = handler;

				try {
					RECORDER.close();
				} catch (Throwable th) {
					LOG.log(Level.WARNING, "Failed to stop the old Flight Recorder instance. The old instance might be still running on the background, wasting resources.", th);
				}
				RECORDER = recorder;

				LOG.log(Level.INFO, "Successfully reconfigured Scriptable JMX Exporter on {0}.", newConfig.server.bindAddress);
			});
			if (watcher instanceof Instrumented)
				MetricRegistry.getInstance().add((Instrumented) watcher);

			final Config initialConfig = watcher.config();
			RECORDER = FlightRecorderModule.getInstance().create(initialConfig.flightRecorderEventRules);
			RECORDER.start();
			HANDLER = new ExporterHttpHandler(initialConfig.rules, initialConfig.options, MetricRegistry.getInstance(), initialConfig.contexts);
			SERVER = newServer(initialConfig.server.bindAddress);
			safeStart(SERVER);
			watcher.start();
			LOG.log(Level.INFO, "Successfully started Scriptable JMX Exporter on {0}.", initialConfig.server.bindAddress);
		} catch (final Throwable th) {
			LOG.log(Level.SEVERE, "Failed to start Scriptable JMX Exporter.", th);
			System.exit(1);
		}
	}
}
