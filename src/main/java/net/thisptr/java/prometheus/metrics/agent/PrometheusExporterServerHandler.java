package net.thisptr.java.prometheus.metrics.agent;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.java.prometheus.metrics.agent.config.Config.OptionsConfig;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.Scraper;

/**
 * https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md
 */
public class PrometheusExporterServerHandler {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Scraper<PrometheusScrapeRule> scraper;
	private final JsonQuery labels;
	private final OptionsConfig options;

	public PrometheusExporterServerHandler(final List<PrometheusScrapeRule> rules, final JsonQuery labels, final OptionsConfig options) {
		this.labels = labels;
		this.options = options;
		this.scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), rules);
	}

	private Map<String, String> makeLabels() throws IOException {
		if (labels == null)
			return Collections.emptyMap();
		final List<JsonNode> nodes = new ArrayList<>();
		labels.apply(RootScope.getInstance(), NullNode.getInstance(), nodes::add);
		if (nodes.isEmpty())
			return Collections.emptyMap();
		final JsonNode in = nodes.get(nodes.size() - 1);
		try (JsonParser jp = MAPPER.treeAsTokens(in)) {
			return MAPPER.readValue(jp, new TypeReference<Map<String, String>>() {});
		} catch (final Exception e) {
			throw new RuntimeException("Cannot deserialize labels from input: " + in, e);
		}
	}

	private OptionsConfig getOptions(final IHTTPSession session) {
		final OptionsConfig options = new OptionsConfig();

		options.includeTimestamp = Optional.ofNullable(session.getParameters().get("include_timestamp"))
				.filter(args -> !args.isEmpty())
				.map(args -> Boolean.parseBoolean(args.get(0)))
				.orElse(this.options.includeTimestamp);

		options.minimumResponseTime = Optional.ofNullable(session.getParameters().get("minimum_response_time"))
				.filter(args -> !args.isEmpty())
				.map(args -> Math.max(0, Math.min(60000L, Long.parseLong(args.get(0)))))
				.orElse(this.options.minimumResponseTime);

		return options;
	}

	public Response handleGetMetrics(final IHTTPSession session) throws InterruptedException, IOException {
		final Map<String, String> labels = makeLabels();
		final OptionsConfig options = getOptions(session);

		final Map<String, List<PrometheusMetric>> allMetrics = new TreeMap<>();
		scraper.scrape(new PrometheusScrapeOutput(RootScope.getInstance(), (metric) -> {
			if (metric.labels == null)
				metric.labels = new HashMap<>();
			metric.labels.putAll(labels);
			allMetrics.computeIfAbsent(metric.name, (name) -> new ArrayList<>()).add(metric);
		}), options.minimumResponseTime, TimeUnit.MILLISECONDS);

		final StringWriter writer = new StringWriter();
		try (PrometheusMetricWriter pwriter = new PrometheusMetricWriter(writer, options.includeTimestamp)) {
			allMetrics.forEach((name, metrics) -> {
				metrics.forEach((metric) -> {
					try {
						pwriter.write(metric);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			});
		}

		return PrometheusExporterServer.newFixedLengthResponse(Response.Status.OK, "text/plain; version=0.0.4; charset=utf-8", writer.toString());
	}

	public Response handleGetMBeans(final IHTTPSession session) throws InterruptedException {
		final StringWriter writer = new StringWriter();
		scraper.scrape((rule, timestamp, value) -> {
			writer.write(value.toString());
			writer.write('\n');
		});
		return PrometheusExporterServer.newFixedLengthResponse(Response.Status.OK, "text/plain; charset=utf-8", writer.toString());
	}

	public Response handleGetMetricsRaw(final IHTTPSession session) throws InterruptedException {
		final StringWriter writer = new StringWriter();
		scraper.scrape(new PrometheusScrapeOutput(RootScope.getInstance(), (metric) -> {}, (raw) -> {
			writer.write(raw.toString());
			writer.write('\n');
		}));
		return PrometheusExporterServer.newFixedLengthResponse(Response.Status.OK, "text/plain; charset=utf-8", writer.toString());
	}
}
