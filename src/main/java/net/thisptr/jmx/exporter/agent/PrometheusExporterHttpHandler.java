package net.thisptr.jmx.exporter.agent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xnio.channels.StreamSinkChannel;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import net.thisptr.jmx.exporter.agent.PrometheusMetricWriter.WritableByteChannelController;
import net.thisptr.jmx.exporter.agent.config.Config.OptionsConfig;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngine.ScriptCompileException;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.scraper.ScrapeOutput;
import net.thisptr.jmx.exporter.agent.scraper.Scraper;

/**
 * https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md
 */
public class PrometheusExporterHttpHandler implements HttpHandler {
	private static final Logger LOG = Logger.getLogger(PrometheusExporterHttpHandler.class.getName());

	private static final PrometheusScrapeRule DEFAULT_RULE;

	static {
		try {
			DEFAULT_RULE = new PrometheusScrapeRule();
			DEFAULT_RULE.transform = ScriptEngineRegistry.getInstance().get("java").compileTransformScript("V1.transform(in, out, \"type\");");
			DEFAULT_RULE.patterns = Collections.emptyList();
		} catch (final ScriptCompileException e) {
			throw new RuntimeException(e);
		}
	}

	private final Scraper<PrometheusScrapeRule> scraper;
	private final OptionsConfig options;

	public PrometheusExporterHttpHandler(final List<PrometheusScrapeRule> rules, final OptionsConfig options) {
		this.options = options;
		this.scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), rules, DEFAULT_RULE);
	}

	private static void parseBooleanQueryParamAndThen(final HttpServerExchange exchange, final String name, final Consumer<Boolean> fn) {
		final Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
		final Deque<String> deque = queryParams.get(name);
		if (deque != null) {
			final String value = deque.getFirst();
			if (!value.isEmpty()) {
				fn.accept(Boolean.parseBoolean(value));
			}
		}
	}

	private static void parseLongQueryParamAndThen(final HttpServerExchange exchange, final String name, final LongConsumer fn) {
		final Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
		final Deque<String> deque = queryParams.get(name);
		if (deque != null) {
			final String value = deque.getFirst();
			if (!value.isEmpty()) {
				fn.accept(Long.parseLong(value));
			}
		}
	}

	private OptionsConfig getOptions(final HttpServerExchange exchange) {
		final OptionsConfig options = new OptionsConfig();

		// values provided in config file
		options.includeTimestamp = this.options.includeTimestamp;
		options.includeHelp = this.options.includeHelp;
		options.includeType = this.options.includeType;
		options.minimumResponseTime = this.options.minimumResponseTime;

		// values from query params
		parseBooleanQueryParamAndThen(exchange, "include_help", (value) -> options.includeHelp = value);
		parseBooleanQueryParamAndThen(exchange, "include_type", (value) -> options.includeType = value);
		parseBooleanQueryParamAndThen(exchange, "include_timestamp", (value) -> options.includeTimestamp = value);
		parseLongQueryParamAndThen(exchange, "minimum_response_time", (value) -> options.minimumResponseTime = Math.max(0, Math.min(60000L, value)));

		return options;
	}

	private static class PrometheusScrapeOutput implements ScrapeOutput<PrometheusScrapeRule> {
		private final PrometheusMetricOutput output;

		public PrometheusScrapeOutput(final PrometheusMetricOutput output) {
			this.output = output;
		}

		@Override
		public void emit(final Sample<PrometheusScrapeRule> sample) {
			try {
				sample.rule.transform.execute(sample, output);
			} catch (Throwable th) {
				LOG.log(Level.WARNING, String.format("Got exception while executing user script for %s:%s (type = %s)", sample.name, sample.attribute.getName(), sample.attribute.getType()), th);
			}
		}
	}

	public void handleGetMetrics(final HttpServerExchange exchange) throws InterruptedException, IOException {
		final OptionsConfig options = getOptions(exchange);

		final Map<String, List<PrometheusMetric>> allMetrics = new TreeMap<>();
		scraper.scrape(new PrometheusScrapeOutput((metric) -> {
			allMetrics.computeIfAbsent(metric.name, (name) -> new ArrayList<>()).add(metric);
		}), options.minimumResponseTime, TimeUnit.MILLISECONDS);

		exchange.setStatusCode(StatusCodes.OK);
		exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; version=0.0.4; charset=utf-8");

		final ByteBufferPool byteBufferPool = exchange.getConnection().getByteBufferPool().getArrayBackedPool();
		final PooledByteBuffer byteBuffer = byteBufferPool.allocate();
		try {
			final StreamSinkChannel channel = exchange.getResponseChannel();
			final WritableByteChannelController controller = channel::awaitWritable;
			try (PrometheusMetricWriter pwriter = new PrometheusMetricWriter(channel, controller, byteBuffer.getBuffer(), options.includeTimestamp)) {
				allMetrics.forEach((name, metrics) -> {
					try {
						if (metrics.isEmpty())
							return;
						final PrometheusMetric m = metrics.get(0);
						if (options.includeHelp) {
							final String help = m.help;
							if (help != null) {
								final boolean totalOfCounter = "total".equals(m.suffix) && "counter".equals(m.type);
								pwriter.writeHelp(name, m.nameWriter, totalOfCounter ? m.suffix : null, help);
							}
						}
						if (options.includeType) {
							final String type = m.type;
							if (type != null) {
								final boolean totalOfCounter = "total".equals(m.suffix) && "counter".equals(m.type);
								pwriter.writeType(name, m.nameWriter, totalOfCounter ? m.suffix : null, type);
							}
						}
						metrics.forEach((metric) -> {
							try {
								pwriter.write(metric);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						});
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			}
		} finally {
			byteBuffer.close();
		}
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		if (exchange.isInIoThread()) {
			exchange.dispatch(this);
			return;
		}
		switch (exchange.getRequestPath()) {
		case "/metrics":
			if (!exchange.getRequestMethod().equalToString("GET")) {
				exchange.setStatusCode(StatusCodes.METHOD_NOT_ALLOWED);
				return;
			}
			handleGetMetrics(exchange);
			break;
		default:
			exchange.setStatusCode(StatusCodes.NOT_FOUND);
			break;
		}
	}
}
