package net.thisptr.java.prometheus.metrics.agent;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.ReflectionException;

import fi.iki.elonen.NanoHTTPD.Response;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.Scraper;

/**
 * https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md
 */
public class PrometheusExporterServerHandler {
	private Scraper<PrometheusScrapeRule> scraper;

	public PrometheusExporterServerHandler(final List<PrometheusScrapeRule> rules) {
		this.scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), rules);
	}

	public Response handleGetMetrics() throws IntrospectionException, InstanceNotFoundException, ReflectionException, IOException {
		final Map<String, List<PrometheusMetric>> allMetrics = new TreeMap<>();
		scraper.scrape(new PrometheusScrapeOutput(RootScope.getInstance(), (metric) -> {
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