package net.thisptr.jmx.exporter.agent.handler.janino.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import com.google.common.collect.Maps;

import net.thisptr.jmx.exporter.agent.handler.janino.api.AttributeValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.misc.MutableInteger;
import net.thisptr.jmx.exporter.agent.utils.MoreClasses;

public class TransformV1Function {
	private static final Logger LOG = Logger.getLogger(TransformV1Function.class.getName());

	public static class ObjectAndAttributeName {
		private final String domain;
		private final Map<String, String> keyProperties;
		private final String attributeName;

		public ObjectAndAttributeName(final String domain, final Map<String, String> keyProperties, final String attributeName) {
			this.domain = domain;
			this.keyProperties = keyProperties;
			this.attributeName = attributeName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
			result = prime * result + ((domain == null) ? 0 : domain.hashCode());
			result = prime * result + ((keyProperties == null) ? 0 : keyProperties.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ObjectAndAttributeName other = (ObjectAndAttributeName) obj;
			if (attributeName == null) {
				if (other.attributeName != null)
					return false;
			} else if (!attributeName.equals(other.attributeName))
				return false;
			if (domain == null) {
				if (other.domain != null)
					return false;
			} else if (!domain.equals(other.domain))
				return false;
			if (keyProperties == null) {
				if (other.keyProperties != null)
					return false;
			} else if (!keyProperties.equals(other.keyProperties))
				return false;
			return true;
		}
	}

	private static final Set<ObjectAndAttributeName> SUPPRESSED_TYPES = Collections.newSetFromMap(new ConcurrentHashMap<ObjectAndAttributeName, Boolean>());

	static class Labels {
		private final Map<String, MutableInteger> counts = new HashMap<>();

		private final List<String> labels = new ArrayList<>();
		private final List<String> values = new ArrayList<>();
		private final List<Integer> dups = new ArrayList<>();

		public void push(final String label, final String value) {
			final int dup = counts.computeIfAbsent(label, (dummy) -> new MutableInteger()).getAndIncrement();
			if (dup == 0) {
				labels.add(label);
			} else {
				labels.add(label + "_" + dup);
			}
			values.add(value);
			dups.add(dup);
		}

		/**
		 * Removes the last pushed label-value pair. Must be invoked in reverse order of push().
		 * 
		 * @param label This must be the same label as given to the corresponding push().
		 */
		public void pop(final String label) {
			if (counts.get(label).decrementAndGet() <= 0)
				counts.remove(label);
			final int index = labels.size() - 1;
			labels.remove(index);
			values.remove(index);
		}

		public void forEach(final BiConsumer<String, String> fn) {
			for (int i = 0; i < labels.size(); ++i)
				fn.accept(labels.get(i), values.get(i));
		}

		public int size() {
			return labels.size();
		}
	}

	private static void unfoldArray(final MetricNamer namer, final Labels labels, final Object arrayValue, final String arrayType, final AttributeValue input, final MetricValueOutput output) {
		final String elementType = MoreClasses.elementTypeNameOf(arrayType);
		int length = Array.getLength(arrayValue);
		for (int i = 0; i < length; ++i) {
			final Object element = Array.get(arrayValue, i);
			labels.push("index", String.valueOf(i));
			unfold(namer, labels, element, elementType, input, output);
			labels.pop("index");
		}
	}

	private static void unfoldCompositeData(final MetricNamer namer, final Labels labels, final CompositeData compositeData, final AttributeValue input, final MetricValueOutput output) {
		final CompositeType compositeType = compositeData.getCompositeType();
		for (final String key : compositeType.keySet()) {
			final int mark = namer.push(key);
			unfold(namer, labels, compositeData.get(key), compositeType.getType(key).getClassName(), input, output);
			namer.pop(mark);
		}
	}

	private static void unfoldTabularData(final MetricNamer namer, final Labels labels, final TabularData tabularData, final AttributeValue input, final MetricValueOutput output) {
		final TabularType tabularType = tabularData.getTabularType();
		final CompositeType rowType = tabularType.getRowType();
		final List<String> indexColumnNames = tabularType.getIndexNames();

		final Set<String> columnNames = rowType.keySet();
		final List<String> nonIndexColumnNames = new ArrayList<>(Math.max(0, columnNames.size() - indexColumnNames.size()));
		for (final String columnName : columnNames) {
			if (indexColumnNames.contains(columnName))
				continue;
			nonIndexColumnNames.add(columnName);
		}
		final List<String> nonIndexColumnTypes = new ArrayList<>(nonIndexColumnNames.size());
		for (final String nonIndexColumnName : nonIndexColumnNames) {
			nonIndexColumnTypes.add(rowType.getType(nonIndexColumnName).getClassName());
		}

		@SuppressWarnings("unchecked")
		final Collection<CompositeData> rows = (Collection<CompositeData>) tabularData.values();
		for (final CompositeData row : rows) {
			for (int i = 0; i < indexColumnNames.size(); ++i) {
				final String indexColumnName = indexColumnNames.get(i);
				// TODO: proper string conversion
				labels.push(indexColumnName, String.valueOf(row.get(indexColumnName)));
			}

			for (int i = 0; i < nonIndexColumnNames.size(); ++i) {
				final String columnName = nonIndexColumnNames.get(i);
				final String columnType = nonIndexColumnTypes.get(i);
				final int mark = namer.push(columnName);
				unfold(namer, labels, row.get(columnName), columnType, input, output);
				namer.pop(mark);
			}

			for (int i = indexColumnNames.size() - 1; i >= 0; --i) { // reverse order
				labels.pop(indexColumnNames.get(i));
			}
		}
	}

	private static void unfoldByDynamicType(final MetricNamer namer, final Labels labels, final Object value, final String type, final AttributeValue input, final MetricValueOutput output) {
		if (value instanceof Number) {
			emit(namer, labels, input, output, ((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			emit(namer, labels, input, output, (Boolean) value ? 1 : 0);
		} else if (value instanceof CompositeData) {
			unfoldCompositeData(namer, labels, (CompositeData) value, input, output);
		} else if (value instanceof TabularData) {
			unfoldTabularData(namer, labels, (TabularData) value, input, output);
		} else {
			if (SUPPRESSED_TYPES.add(new ObjectAndAttributeName(input.domain, input.keyProperties, input.attributeName)))
				LOG.warning(String.format("Got unsupported dynamic type \"%s\" while processing %s:%s. Further warnings are suppressed for the attribute.", type, formatObjectNameForLogging(input.domain, input.keyProperties), input.attributeName));
		}
		// TODO: handle arrays, characters, strings and ObjectName
	}

	/**
	 * @param namer
	 * @param labels
	 * @param names
	 * @param value
	 * @param type   a string representation of the type of the value. The format is the same as {@link Class#getName()}.
	 * @param input
	 * @param output
	 */
	private static void unfold(final MetricNamer namer, final Labels labels, final Object value, final String type, final AttributeValue input, final MetricValueOutput output) {
		switch (type) {
		case "double": /* fall through */
		case "float": /* fall through */
		case "int": /* fall through */
		case "long": /* fall through */
		case "short": /* fall through */
		case "byte": /* fall through */
		case "java.lang.Double": /* fall through */
		case "java.lang.Float": /* fall through */
		case "java.lang.Integer": /* fall through */
		case "java.lang.Long": /* fall through */
		case "java.lang.Short": /* fall through */
		case "java.lang.Byte":
			if (value == null)
				break;
			emit(namer, labels, input, output, ((Number) value).doubleValue());
			break;
		case "boolean": /* fall through */
		case "java.lang.Boolean":
			if (value == null)
				break;
			emit(namer, labels, input, output, (Boolean) value ? 1 : 0);
			break;
		case "javax.management.ObjectName": /* fall through */
		case "java.lang.String":
			// Prometheus doesn't support textual values. Skip.
			break;
		case "char": /* fall through */
		case "java.lang.Character":
			if (value == null)
				break;
			emit(namer, labels, input, output, ((Character) value).charValue());
			break;
		case "javax.management.openmbean.CompositeData":
			if (value == null) // we can't reliably determine the details of the CompositeData type.
				break;
			unfoldCompositeData(namer, labels, (CompositeData) value, input, output);
			break;
		case "javax.management.openmbean.TabularData":
			if (value == null)
				break;
			unfoldTabularData(namer, labels, (TabularData) value, input, output);
			break;
		case "java.lang.Object":
			// If the reported type is java.lang.Object, let's try to guess from the actual value.
			if (value == null)
				break;
			unfoldByDynamicType(namer, labels, value, type, input, output);
			break;
		default:
			if (type.startsWith("[")) { // array type
				if (value == null)
					break;
				unfoldArray(namer, labels, value, type, input, output);
			} else {
				if (SUPPRESSED_TYPES.add(new ObjectAndAttributeName(input.domain, input.keyProperties, input.attributeName)))
					LOG.warning(String.format("Got unsupported type \"%s\" while processing %s:%s. Further warnings are suppressed for the attribute.", type, formatObjectNameForLogging(input.domain, input.keyProperties), input.attributeName));
				break;
			}
		}
	}

	/**
	 * Formats human-readable ObjectName for logging or debugging purpose. Do not expect consistent formatting.
	 * The format is subject to change without notice.
	 * 
	 * @param domain
	 * @param keyProperties
	 * @return
	 */
	private static String formatObjectNameForLogging(final String domain, final Map<String, String> keyProperties) {
		final StringBuilder builder = new StringBuilder();
		builder.append(domain);
		builder.append(':');
		String sep = "";
		for (final Entry<String, String> entry : keyProperties.entrySet()) {
			builder.append(sep);
			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
			sep = ",";
		}
		return builder.toString();
	}

	private static void emit(final MetricNamer namer, final Labels labels, final AttributeValue input, final MetricValueOutput output, final double value) {
		final Map<String, String> metricLabels = Maps.newHashMapWithExpectedSize(labels.size());
		labels.forEach((label, labelValue) -> {
			metricLabels.put(label, labelValue);
		});

		final MetricValue m = new MetricValue();
		m.name = namer.toString();
		m.value = value;
		m.labels = metricLabels;
		m.timestamp = input.timestamp;
		m.help = input.attributeDescription;
		output.emit(m);
	}

	private static class MetricNamer {
		private final StringBuilder nameBuilder;

		public MetricNamer(final String domain, final Map<String, String> keyProperties, final String attributeName, final String[] nameKeys) {
			this.nameBuilder = new StringBuilder();
			this.nameBuilder.append(domain);
			for (final String nameKey : nameKeys) {
				final String metricLabelValue = keyProperties.get(nameKey);
				if (metricLabelValue == null)
					continue;
				this.nameBuilder.append(':');
				this.nameBuilder.append(metricLabelValue);
			}
			this.nameBuilder.append(':');
			this.nameBuilder.append(attributeName);
		}

		public int push(final String name) {
			final int length = nameBuilder.length();
			nameBuilder.append('_');
			nameBuilder.append(name);
			return length;
		}

		/**
		 * @param mark which was returned by the corresponding push()
		 */
		public void pop(final int mark) {
			nameBuilder.setLength(mark);
		}

		public String toString() {
			return nameBuilder.toString();
		}
	}

	private static boolean contains(final String[] set, final String value) {
		// This logic is O(N), but is justified as N is usually 1 or 2.
		for (final String elementInSet : set)
			if (elementInSet.equals(value))
				return true;
		return false;
	}

	public static void transformV1(final AttributeValue sample, final MetricValueOutput output, final String... propertiesToUseAsMetricName) {
		final MetricNamer namer = new MetricNamer(sample.domain, sample.keyProperties, sample.attributeName, propertiesToUseAsMetricName);

		final Labels labels = new Labels();
		sample.keyProperties.forEach((k, v) -> {
			if (contains(propertiesToUseAsMetricName, k))
				return;
			labels.push(k, v);
		});

		unfold(namer, labels, sample.value, sample.attributeType, sample, output);
	}
}
