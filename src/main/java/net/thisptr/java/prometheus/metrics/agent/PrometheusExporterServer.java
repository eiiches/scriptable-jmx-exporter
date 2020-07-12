package net.thisptr.java.prometheus.metrics.agent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.net.HostAndPort;

import fi.iki.elonen.NanoHTTPD;

public class PrometheusExporterServer extends NanoHTTPD {
	private static final Logger LOG = Logger.getLogger(PrometheusExporterServer.class.getName());

	private volatile PrometheusExporterServerHandler handler;

	public PrometheusExporterServer(final HostAndPort bindAddress, final PrometheusExporterServerHandler handler) {
		super(bindAddress.getHost(), bindAddress.getPortOrDefault(18090));
		this.handler = handler;
	}

	private Response dispatch(final IHTTPSession session) {
		try {
			switch (session.getUri()) {
			case "/metrics":
				if (session.getMethod() != Method.GET)
					return handleMethodNotAllowed();
				return handler.handleGetMetrics(session);
			case "/metrics.json":
				if (session.getMethod() != Method.GET)
					return handleMethodNotAllowed();
				return handler.handleGetMetricsJson(session);
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

	public void configure(final PrometheusExporterServerHandler handler) {
		this.handler = handler;
	}
}
