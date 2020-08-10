package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import java.util.Map;

import net.thisptr.jmx.exporter.agent.handler.janino.api.AttributeValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.handler.janino.api._InternalUseDoNotImportProxyAccessor;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.Labels;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.LowerCaseWriter;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.MetricNamer;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.SnakeCaseWriter;
import net.thisptr.jmx.exporter.agent.handler.janino.internal.ValueTransformations;
import net.thisptr.jmx.exporter.agent.utils.MoreArrays;

public class V1 {
	public interface MetricValueModifier {
		void apply(MetricValue m);
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

	private final static char DEFAULT_SEPARATOR = '_';

	public static void transform(final AttributeValue in, final MetricValueOutput out, final MetricValueModifier... modifiers) {
		transformInternal(in, out, DEFAULT_SEPARATOR, modifiers);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final MetricValueModifier... modifiers) {
		transformInternal(in, out, DEFAULT_SEPARATOR, modifiers, key1);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final MetricValueModifier... modifiers) {
		transformInternal(in, out, DEFAULT_SEPARATOR, modifiers, key1, key2);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final MetricValueModifier... modifiers) {
		transformInternal(in, out, DEFAULT_SEPARATOR, modifiers, key1, key2, key3);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final String key4, final MetricValueModifier... modifiers) {
		transformInternal(in, out, DEFAULT_SEPARATOR, modifiers, key1, key2, key3, key4);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String key1, final String key2, final String key3, final String key4, final String key5, final MetricValueModifier... modifiers) {
		transformInternal(in, out, DEFAULT_SEPARATOR, modifiers, key1, key2, key3, key4, key5);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final String[] keys, final MetricValueModifier... modifiers) {
		transformInternal(in, out, DEFAULT_SEPARATOR, modifiers, keys);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final char sep, final MetricValueModifier... modifiers) {
		transformInternal(in, out, sep, modifiers);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final char sep, final String key1, final MetricValueModifier... modifiers) {
		transformInternal(in, out, sep, modifiers, key1);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final char sep, final String key1, final String key2, final MetricValueModifier... modifiers) {
		transformInternal(in, out, sep, modifiers, key1, key2);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final char sep, final String key1, final String key2, final String key3, final MetricValueModifier... modifiers) {
		transformInternal(in, out, sep, modifiers, key1, key2, key3);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final char sep, final String key1, final String key2, final String key3, final String key4, final MetricValueModifier... modifiers) {
		transformInternal(in, out, sep, modifiers, key1, key2, key3, key4);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final char sep, final String key1, final String key2, final String key3, final String key4, final String key5, final MetricValueModifier... modifiers) {
		transformInternal(in, out, sep, modifiers, key1, key2, key3, key4, key5);
	}

	public static void transform(final AttributeValue in, final MetricValueOutput out, final char sep, final String[] keys, final MetricValueModifier... modifiers) {
		transformInternal(in, out, sep, modifiers, keys);
	}

	private static void transformInternal(final AttributeValue in, final MetricValueOutput out, final char sep, final MetricValueModifier[] modifiers, final String... nameKeys) {
		final Builder builder = V1.name(sep, in.domain);
		for (final String nameKey : nameKeys) {
			final String value = in.keyProperties.get(nameKey);
			if (value == null)
				continue;
			builder.appendName(value);
		}
		if (in.attributeName != null)
			builder.appendName(in.attributeName);
		builder.addLabelsExcluding(in.keyProperties, nameKeys);
		builder.timestamp(in.timestamp);
		builder.help(in.attributeDescription);
		builder.transform(in.value, in.attributeType, out, '_', modifiers);
		builder.dispose();
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

		public Builder transform(final Object value, final String type, final MetricValueOutput out, final MetricValueModifier... modifiers) {
			return transform(value, type, out, '.', modifiers); // always use '.' for nested attributes
		}

		private Builder transform(final Object value, final String type, final MetricValueOutput out, final char separator, final MetricValueModifier... modifiers) {
			namer.separator(separator);
			ValueTransformations.unfold(namer, labels, value, type, (m) -> {
				m.timestamp = this.timestamp;
				m.help = this.help;
				m.type = this.type;
				m.suffix = this.suffix;
				for (final MetricValueModifier modifier : modifiers)
					modifier.apply(m);
				out.emit(m);
			});
			return this;
		}

		public void dispose() {
			reset();
			V1.BUILDER_CACHE.get().builder = this;
		}
	}

	private static final class ThreadLocalCacheHolder {
		public Builder builder;
	}

	private static final ThreadLocal<ThreadLocalCacheHolder> BUILDER_CACHE = new ThreadLocal<ThreadLocalCacheHolder>() {
		@Override
		protected ThreadLocalCacheHolder initialValue() {
			return new ThreadLocalCacheHolder();
		};
	};

	public static Builder name(final char separator, final String... names) {
		final Builder builder;
		final ThreadLocalCacheHolder holder = BUILDER_CACHE.get();
		if (holder.builder != null) {
			builder = holder.builder;
			holder.builder = null;
		} else {
			builder = new Builder();
		}
		return builder.separator(separator).appendName(names);
	}

	public static Builder name(final String... names) {
		return name('_', names);
	}
}
