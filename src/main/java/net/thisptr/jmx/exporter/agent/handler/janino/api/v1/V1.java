package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import net.thisptr.jmx.exporter.agent.handler.janino.api.AttributeValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.handler.janino.api._InternalUseDoNotImportProxyAccessor;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.TransformV1Function;
import net.thisptr.jmx.exporter.agent.misc.StringWriter;

public class V1 {
	public interface MetricValueModifier {
		void apply(MetricValue m);
	}

	// @VisibleForTesting
	static class SnakeCaseWriter implements StringWriter {
		private static final SnakeCaseWriter INSTANCE = new SnakeCaseWriter();

		public static SnakeCaseWriter getInstance() {
			return INSTANCE;
		}

		@Override
		public int expectedSize(final String name) {
			// worst case: all CAPITAL -> c_a_p_i_t_a_l
			return Math.max(1, name.length() * 2);
		}

		@Override
		public int write(final String name, final byte[] bytes, int index) {
			final int savedIndex = index;
			int length = name.length();
			boolean underscore = false;
			for (int i = 0; i < length; ++i) {
				final char ch = name.charAt(i);
				if ('a' <= ch && ch <= 'z') {
					bytes[index++] = (byte) ch;
					underscore = false;
				} else if ('A' <= ch && ch <= 'Z') {
					if (!underscore && savedIndex != index)
						bytes[index++] = '_';
					bytes[index++] = (byte) (ch + ('a' - 'A'));
					underscore = false;
				} else if (ch == ':' || ch == '_') {
					if (!underscore) {
						bytes[index++] = '_';
						underscore = true;
					}
				} else if ('0' <= ch && ch <= '9') {
					if (savedIndex == index)
						bytes[index++] = '_'; // first char cannot be a number; prepend _;
					bytes[index++] = (byte) ch;
					underscore = false;
				} else {
					if (Character.isHighSurrogate(ch))
						++i;
					if (!underscore) {
						bytes[index++] = '_';
						underscore = true;
					}
				}
			}
			if (savedIndex == index) { // empty metric name is not allowed
				bytes[index++] = '_';
			}
			return index;
		}
	}

	private static final MetricValueModifier SNAKE_CASE = (m) -> {
		_InternalUseDoNotImportProxyAccessor.setNameWriter(m, SnakeCaseWriter.getInstance());
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

	private static final MetricValueModifier COUNTER = (m) -> {
		m.type = "counter";
	};

	public static MetricValueModifier counter() {
		return COUNTER;
	}

	private static final MetricValueModifier HISTOGRAM = (m) -> {
		m.type = "histogram";
	};

	public static MetricValueModifier histogram() {
		return HISTOGRAM;
	}

	private static final MetricValueModifier SUMMARY = (m) -> {
		m.type = "summary";
	};

	public static MetricValueModifier summary() {
		return SUMMARY;
	}

	public static MetricValueModifier suffix(final String suffix) {
		return (m) -> {
			m.suffix = suffix;
		};
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

	public static void transform(final AttributeValue in, final MetricValueOutput out, final MetricValueModifier... modifiers) {
		TransformV1Function.transformV1(in, modify(out, modifiers));
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
