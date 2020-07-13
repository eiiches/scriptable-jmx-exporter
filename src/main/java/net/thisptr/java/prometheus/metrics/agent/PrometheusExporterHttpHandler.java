package net.thisptr.java.prometheus.metrics.agent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Joiner;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.java.prometheus.metrics.agent.config.Config.OptionsConfig;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.Scraper;

/**
 * https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md
 */
public class PrometheusExporterHttpHandler implements HttpHandler {
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

	private OptionsConfig getOptions(final HttpServerExchange exchange) {
		final OptionsConfig options = new OptionsConfig();
		options.includeTimestamp = this.options.includeTimestamp;
		options.includeHelp = this.options.includeHelp;
		options.minimumResponseTime = this.options.minimumResponseTime;

		final Map<String, Deque<String>> queryParams = exchange.getQueryParameters();

		Deque<String> deque = queryParams.get("include_timestamp");
		if (deque != null) {
			final String value = deque.getFirst();
			if (!value.isEmpty()) {
				options.includeTimestamp = Boolean.parseBoolean(value);
			}
		}

		deque = queryParams.get("include_help");
		if (deque != null) {
			final String value = deque.getFirst();
			if (!value.isEmpty()) {
				options.includeHelp = Boolean.parseBoolean(value);
			}
		}

		deque = queryParams.get("minimum_response_time");
		if (deque != null) {
			final String value = deque.getFirst();
			if (!value.isEmpty()) {
				options.minimumResponseTime = Math.max(0, Math.min(60000L, Long.parseLong(value)));
			}
		}

		return options;
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

		exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; version=0.0.4; charset=utf-8");
		exchange.setStatusCode(StatusCodes.OK);

		final StringBuilder builder = new StringBuilder();
		try (PrometheusMetricWriter pwriter = new PrometheusMetricWriter(builder, options.includeTimestamp)) {
			allMetrics.forEach((name, metrics) -> {
				final Set<String> helps = new HashSet<>();
				metrics.forEach((metric) -> {
					helps.add(metric.help);
				});
				if (options.includeHelp)
					pwriter.writeHelp(name, Joiner.on(" / ").join(helps));
				metrics.forEach((metric) -> {
					try {
						pwriter.write(metric);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			});
		}

		exchange.getResponseSender().send(builder.toString());
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		try {
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
		} catch (Throwable th) {
			exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				try (PrintWriter writer = new PrintWriter(baos)) {
					th.printStackTrace(writer);
				}
				exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=utf-8");
				exchange.getResponseSender().send(ByteBuffer.wrap(baos.toByteArray()));
			}
		} finally {
			exchange.endExchange();
		}
	}
}
