package net.thisptr.jmx.exporter.agent.scripting.janino.api.fn;

import java.util.logging.Logger;

import net.thisptr.jmx.exporter.agent.scripting.janino.internal.ValueTransformations;

public class LogFunction {
	private static final Logger LOG = Logger.getLogger(ValueTransformations.class.getName());

	public static void log(String format, final Object... args) {
		LOG.info(String.format(format, args));
	}

	public static void log(Object format) {
		LOG.info(format.toString());
	}
}
