package net.thisptr.java.prometheus.metrics.agent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Joiner;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.java.prometheus.metrics.agent.config.Config.OptionsConfig;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.misc.StreamSinkChannelOutputStream;
import net.thisptr.java.prometheus.metrics.agent.scraper.ScrapeOutput;
import net.thisptr.java.prometheus.metrics.agent.scraper.Scraper;

/**
 * https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md
 */
public class PrometheusExporterHttpHandler implements HttpHandler {
	private static final Logger LOG = Logger.getLogger(PrometheusExporterHttpHandler.class.getName());
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Scraper<PrometheusScrapeRule> scraper;
	private final JsonQuery labels;
	private final OptionsConfig options;

	public PrometheusExporterHttpHandler(final List<PrometheusScrapeRule> rules, final JsonQuery labels, final OptionsConfig options) {
		this.labels = labels;
		this.options = options;
		this.scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), rules);
	}

	private Map<String, JsonNode> makeLabels() throws IOException {
		if (labels == null)
			return Collections.emptyMap();
		final List<JsonNode> nodes = new ArrayList<>();
		labels.apply(RootScope.getInstance(), NullNode.getInstance(), nodes::add);
		if (nodes.isEmpty())
			return Collections.emptyMap();
		final JsonNode in = nodes.get(nodes.size() - 1);
		try (JsonParser jp = MAPPER.treeAsTokens(in)) {
			return MAPPER.readValue(jp, new TypeReference<Map<String, JsonNode>>() {});
		} catch (final Exception e) {
			throw new RuntimeException("Cannot deserialize labels from input: " + in, e);
		}
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
		final Map<String, JsonNode> labels = makeLabels();
		final OptionsConfig options = getOptions(exchange);

		final Map<String, List<PrometheusMetric>> allMetrics = new TreeMap<>();
		scraper.scrape(new PrometheusScrapeOutput((metric) -> {
			if (metric.labels == null)
				metric.labels = new HashMap<>();
			labels.forEach((label, value) -> {
				metric.labels.put(label, value == null ? null : (value.isTextual() ? value.asText() : value.toString()));
			});
			allMetrics.computeIfAbsent(metric.name, (name) -> new ArrayList<>()).add(metric);
		}), options.minimumResponseTime, TimeUnit.MILLISECONDS);

		exchange.setStatusCode(StatusCodes.OK);
		exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; version=0.0.4; charset=utf-8");

		final ByteBufferPool byteBufferPool = exchange.getConnection().getByteBufferPool();
		final PooledByteBuffer byteBuffer = byteBufferPool.allocate();
		try {
			final StreamSinkChannelOutputStream os = new StreamSinkChannelOutputStream(byteBuffer.getBuffer(), exchange.getResponseChannel());
			try (PrometheusMetricWriter pwriter = new PrometheusMetricWriter(os, options.includeTimestamp)) {
				allMetrics.forEach((name, metrics) -> {
					try {
						if (metrics.isEmpty())
							return;
						if (options.includeHelp) {
							final Set<String> helps = new HashSet<>();
							metrics.forEach((metric) -> {
								helps.add(metric.help);
							});
							pwriter.writeHelp(name, Joiner.on(" / ").join(helps));
						}
						if (options.includeType) {
							final String type = metrics.get(0).type;
							if (type != null) {
								pwriter.writeType(name, type);
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
