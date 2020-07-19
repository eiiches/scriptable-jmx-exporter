package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import java.util.regex.Pattern;

import com.google.common.base.CaseFormat;

import net.thisptr.jmx.exporter.agent.handler.janino.api.AttributeValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.TransformV1Function;

public class V1 {
	private static final Pattern COLON_UNDERSCORE = Pattern.compile("[:_]+");

	public interface MetricValueModifier {
		void apply(MetricValue m);
	}

	private static final MetricValueModifier SNAKE_CASE = (m) -> {
		m.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, m.name);
		m.name = COLON_UNDERSCORE.matcher(m.name).replaceAll("_");
	};

	public static MetricValueModifier snakeCase() {
		return SNAKE_CASE;
	}

	private static final MetricValueModifier GAUGE = (m) -> {
		m.type = "gauge";
	};

	public static MetricValueModifier gauge() {
		return GAUGE;
	}

	private static final MetricValueOutput modify(final MetricValueOutput out, final MetricValueModifier... modifiers) {
		if (modifiers.length == 0)
			return out;
		return (m) -> {
			for (final MetricValueModifier modifier : modifiers)
				modifier.apply(m);
			out.emit(m);
		};
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final MetricValueModifier... modifiers) {
		TransformV1Function.transformV1(in, modify(out, modifiers), key1);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final MetricValueModifier... modifiers) {
		TransformV1Function.transformV1(in, modify(out, modifiers), key1, key2);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final MetricValueModifier... modifiers) {
		TransformV1Function.transformV1(in, modify(out, modifiers), key1, key2, key3);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final String key4, final MetricValueModifier... modifiers) {
		TransformV1Function.transformV1(in, modify(out, modifiers), key1, key2, key3, key4);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final String key4, final String key5, final MetricValueModifier... modifiers) {
		TransformV1Function.transformV1(in, modify(out, modifiers), key1, key2, key3, key4, key5);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String[] keys, final MetricValueModifier... modifiers) {
		TransformV1Function.transformV1(in, modify(out, modifiers), keys);
	}
}
