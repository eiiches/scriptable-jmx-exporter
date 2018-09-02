package net.thisptr.java.prometheus.metrics.agent;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.ReflectionException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import fi.iki.elonen.NanoHTTPD.Response;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.Scraper;

/**
 * https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md
 */
public class PrometheusExporterServerHandler {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Scraper<PrometheusScrapeRule> scraper;
	private final JsonQuery labels;

	public PrometheusExporterServerHandler(final List<PrometheusScrapeRule> rules, final JsonQuery labels) {
		this.labels = labels;
		this.scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), rules);
	}

	private Map<String, String> makeLabels() throws IOException {
		if (labels == null)
			return Collections.emptyMap();
		final List<JsonNode> nodes = labels.apply(RootScope.getInstance(), NullNode.getInstance());
		if (nodes.isEmpty())
			return Collections.emptyMap();
		try (JsonParser jp = MAPPER.treeAsTokens(nodes.get(nodes.size() - 1))) {
			return MAPPER.readValue(jp, new TypeReference<Map<String, String>>() {});
		}
	}

	public Response handleGetMetrics() throws IntrospectionException, InstanceNotFoundException, ReflectionException, IOException {
		final Map<String, String> labels = makeLabels();

		final Map<String, List<PrometheusMetric>> allMetrics = new TreeMap<>();
		scraper.scrape(new PrometheusScrapeOutput(RootScope.getInstance(), (metric) -> {
			if (metric.labels == null)
				metric.labels = new HashMap<>();
			metric.labels.putAll(labels);
			allMetrics.computeIfAbsent(metric.name, (name) -> new ArrayList<>()).add(metric);
		}));

		final StringWriter writer = new StringWriter();
		try (PrometheusMetricWriter pwriter = new PrometheusMetricWriter(writer)) {
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

	public Response handleGetMBeans() throws IntrospectionException, InstanceNotFoundException, ReflectionException, IOException {
		final StringWriter writer = new StringWriter();
		scraper.scrape((rule, value) -> {
			writer.write(value.toString());
			writer.write('\n');
		});
		return PrometheusExporterServer.newFixedLengthResponse(Response.Status.OK, "text/plain; charset=utf-8", writer.toString());
	}

	public Response handleGetMetricsRaw() throws IntrospectionException, InstanceNotFoundException, ReflectionException, IOException {
		final StringWriter writer = new StringWriter();
		scraper.scrape(new PrometheusScrapeOutput(RootScope.getInstance(), (metric) -> {}, (raw) -> {
			writer.write(raw.toString());
			writer.write('\n');
		}));
		return PrometheusExporterServer.newFixedLengthResponse(Response.Status.OK, "text/plain; charset=utf-8", writer.toString());
	}
}