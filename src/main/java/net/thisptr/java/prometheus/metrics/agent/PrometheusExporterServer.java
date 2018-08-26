package net.thisptr.java.prometheus.metrics.agent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.ReflectionException;

import fi.iki.elonen.NanoHTTPD;
import net.thisptr.java.prometheus.metrics.agent.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.scraper.Scraper;

/**
 * https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md
 */
public class PrometheusExporterServer extends NanoHTTPD {
	private static final Logger LOG = Logger.getLogger(PrometheusExporterServer.class.getName());

	private final Scraper<PrometheusScrapeRule> scraper;

	public PrometheusExporterServer(final Config config) {
		super(config.server.bindAddress.getHost(), config.server.bindAddress.getPortOrDefault(18090));
		this.scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), config.rules);
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
		return newFixedLengthResponse(Response.Status.OK, "text/plain; version=0.0.4; charset=utf-8", writer.toString());
	}

	public Response handleGetMBeans() throws IntrospectionException, InstanceNotFoundException, ReflectionException, IOException {
		final StringWriter writer = new StringWriter();
		scraper.scrape((rule, value) -> {
			writer.write(value.toString());
			writer.write('\n');
		});
		return newFixedLengthResponse(Response.Status.OK, "text/plain; charset=utf-8", writer.toString());
	}

	public Response handleGetMetricsRaw() throws IntrospectionException, InstanceNotFoundException, ReflectionException, IOException {
		final StringWriter writer = new StringWriter();
		scraper.scrape(new PrometheusScrapeOutput(RootScope.getInstance(), (metric) -> {}, (raw) -> {
			writer.write(raw.toString());
			writer.write('\n');
		}));
		return newFixedLengthResponse(Response.Status.OK, "text/plain; charset=utf-8", writer.toString());
	}

	private Response dispatch(final IHTTPSession session) {
		try {
			switch (session.getUri()) {
				case "/metrics":
					if (session.getMethod() != Method.GET)
						return handleMethodNotAllowed();
					return handleGetMetrics();
				case "/mbeans":
					if (session.getMethod() != Method.GET)
						return handleMethodNotAllowed();
					return handleGetMBeans();
				case "/metrics-raw":
					if (session.getMethod() != Method.GET)
						return handleMethodNotAllowed();
					return handleGetMetricsRaw();
			}
		} catch (final Throwable th) {
			return handleInternalError(th);
		}
		return handleNotFound();
	}

	private static Response handleMethodNotAllowed() {
		return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, null, null);
	}

	private static Response handleNotFound() {
		return newFixedLengthResponse(Response.Status.NOT_FOUND, null, null);
	}

	private static Response handleInternalError(final Throwable th) {
		final StringWriter writer = new StringWriter();
		try (PrintWriter pwriter = new PrintWriter(writer)) {
			th.printStackTrace(pwriter);
		}
		return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", writer.toString());
	}

	@Override
	public Response serve(final IHTTPSession session) {
		final Response response = dispatch(session);
		LOG.log(Level.FINE, session.getRemoteIpAddress() + " " + session.getMethod() + " " + session.getUri() + " " + response.getStatus().getRequestStatus());
		return response;
	}
}
