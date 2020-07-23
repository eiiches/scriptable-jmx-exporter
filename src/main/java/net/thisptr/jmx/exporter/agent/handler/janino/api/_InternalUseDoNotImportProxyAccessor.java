package net.thisptr.jmx.exporter.agent.handler.janino.api;

import net.thisptr.jmx.exporter.agent.misc.StringWriter;

/**
 * This class is for internal use only. Do not import from user scripts.
 */
public class _InternalUseDoNotImportProxyAccessor {

	public static void setNameWriter(final MetricValue value, final StringWriter writer) {
		value.nameWriter = writer;
	}

	public static StringWriter getNameWriter(final MetricValue value) {
		return value.nameWriter;
	}
}
