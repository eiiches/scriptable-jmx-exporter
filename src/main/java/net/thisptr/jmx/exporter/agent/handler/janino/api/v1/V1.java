package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import java.util.Map;

import net.thisptr.jmx.exporter.agent.handler.janino.api.AttributeValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.handler.janino.api._InternalUseDoNotImportProxyAccessor;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.Labels;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.MetricNamer;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.ValueTransformations;
import net.thisptr.jmx.exporter.agent.misc.StringWriter;
import net.thisptr.jmx.exporter.agent.utils.MoreArrays;

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

	static class LowerCaseWriter implements StringWriter {
		private static final LowerCaseWriter INSTANCE = new LowerCaseWriter();

		public static LowerCaseWriter getInstance() {
			return INSTANCE;
		}

		@Override
		public int expectedSize(final String name) {
			return name.length() + 1;
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
					bytes[index++] = (byte) (ch + ('a' - 'A'));
					underscore = false;
				} else if (ch == '_' || ch == ':') {
					if (!underscore) {
						bytes[index++] = (byte) ch;
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

	private static final MetricValueModifier LOWER_CASE = (m) -> {
		_InternalUseDoNotImportProxyAccessor.setNameWriter(m, LowerCaseWriter.getInstance());
	};

	public static MetricValueModifier snakeCase() {
		return SNAKE_CASE;
	}

	public static MetricValueModifier lowerCase() {
		return LOWER_CASE;
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

	public static void transform(final AttributeValue in, final MetricValueOutput out, final MetricValueModifier... modifiers) {
		transformInternal(in, out, modifiers);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final MetricValueModifier... modifiers) {
		transformInternal(in, out, modifiers, key1);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final MetricValueModifier... modifiers) {
		transformInternal(in, out, modifiers, key1, key2);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final MetricValueModifier... modifiers) {
		transformInternal(in, out, modifiers, key1, key2, key3);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final String key4, final MetricValueModifier... modifiers) {
		transformInternal(in, out, modifiers, key1, key2, key3, key4);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final String key4, final String key5, final MetricValueModifier... modifiers) {
		transformInternal(in, out, modifiers, key1, key2, key3, key4, key5);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String[] keys, final MetricValueModifier... modifiers) {
		transformInternal(in, out, modifiers, keys);
	}

	private static void transformInternal(final AttributeValue in, final MetricValueOutput out, final MetricValueModifier[] modifiers, final String... nameKeys) {
		final MetricNamer namer = new MetricNamer(0); // FIXME: set expected size
		namer.push(in.domain);
		for (final String nameKey : nameKeys) {
			final String value = in.keyProperties.get(nameKey);
			if (value == null)
				continue;
			namer.push(value);
		}
		if (in.attributeName != null)
			namer.push(in.attributeName);

		final int expectedNumLabels = in.keyProperties.size() - nameKeys.length;
		final Labels labels = new Labels(Math.max(0, expectedNumLabels));
		in.keyProperties.forEach((k, v) -> {
			if (MoreArrays.contains(nameKeys, k))
				return;
			labels.push(k, v);
		});

		namer.separator('_');
		ValueTransformations.unfold(namer, labels, in.value, in.attributeType, (m) -> {
			m.timestamp = in.timestamp;
			m.help = in.attributeDescription;
			for (final MetricValueModifier modifier : modifiers)
				modifier.apply(m);
			out.emit(m);
		});
	}

	public static class Builder {
		private final MetricNamer namer = new MetricNamer(0); // FIXME: set expected size
		private final Labels labels = new Labels(0); // FIXME: set expected size
		private String suffix;
		private long timestamp;
		private String type;
		private String help;

		public Builder separator(final char sep) {
			this.namer.separator(sep);
			return this;
		}

		public Builder appendName(final String... names) {
			for (final String name : names)
				if (name != null)
					namer.push(name);
			return this;
		}

		public Builder name(final String... names) {
			final char savedSeparator = namer.separator();
			namer.clear();
			namer.separator(savedSeparator);
			appendName(names);
			return this;
		}

		public Builder addLabels(final Map<String, String> labels) {
			labels.forEach((k, v) -> {
				this.labels.push(k, v);
			});
			return this;
		}

		public Builder addLabelsExcluding(final Map<String, String> labels, final String... names) {
			labels.forEach((k, v) -> {
				if (MoreArrays.contains(names, k))
					return;
				this.labels.push(k, v);
			});
			return this;
		}

		public Builder addLabel(final String name, final String value) {
			this.labels.push(name, value);
			return this;
		}

		public Builder suffix(final String suffix) {
			this.suffix = suffix;
			return this;
		}

		public Builder type(final String type) {
			this.type = type;
			return this;
		}

		public Builder timestamp(final long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder help(final String help) {
			this.help = help;
			return this;
		}

		public void reset() {
			namer.reset();
			labels.clear();
			suffix = null;
			timestamp = 0;
			type = null;
			help = null;
		}

		public void transform(final Object value, final String type, final MetricValueOutput out, final MetricValueModifier... modifiers) {
			namer.separator('.'); // always use '.' for nested attributes
			ValueTransformations.unfold(namer, labels, value, type, (m) -> {
				m.timestamp = this.timestamp;
				m.help = this.help;
				m.type = this.type;
				m.suffix = this.suffix;
				for (final MetricValueModifier modifier : modifiers)
					modifier.apply(m);
				out.emit(m);
			});
		}
	}

	public static Builder name(final char separator, final String... names) {
		return new Builder().separator(separator).appendName(names);
	}

	public static Builder name(final String... names) {
		return new Builder().separator(':').appendName(names);
	}
}
