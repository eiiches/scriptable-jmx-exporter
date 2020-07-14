package net.thisptr.java.prometheus.metrics.agent.handler.janino.functions;

import java.util.regex.Pattern;

import com.google.common.base.CaseFormat;

import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.MetricValueOutput;

public class SnakeCasingFunction {
	private static final Pattern COLON_UNDERSCORE = Pattern.compile("[:_]+");

	public static MetricValueOutput snakeCasing(final MetricValueOutput out) {
		return (m) -> {
			m.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, m.name);
			m.name = COLON_UNDERSCORE.matcher(m.name).replaceAll("_");
			out.emit(m);
		};
	}
}
