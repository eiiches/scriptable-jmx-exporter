package net.thisptr.java.prometheus.metrics.agent.handler.janino.api.fn;

import java.util.logging.Logger;

import net.thisptr.java.prometheus.metrics.agent.handler.janino.internal.TransformV1Function;

public class LogFunction {
	private static final Logger LOG = Logger.getLogger(TransformV1Function.class.getName());

	public static void log(String format, final Object... args) {
		LOG.info(String.format(format, args));
	}

	public static void log(Object format) {
		LOG.info(format.toString());
	}
}
