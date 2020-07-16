package net.thisptr.java.prometheus.metrics.agent.handler.janino.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

import net.thisptr.java.prometheus.metrics.agent.handler.janino.api.AttributeValue;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.api.MetricValue;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.java.prometheus.metrics.agent.misc.MutableInteger;
import net.thisptr.java.prometheus.metrics.agent.utils.MoreClasses;

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
			labels.add(label);
			values.add(value);
			dups.add(Integer.valueOf(counts.computeIfAbsent(label, (dummy) -> new MutableInteger()).getAndIncrement()));
		}

		public void pop() {
			final int index = labels.size() - 1;
			final String label = labels.get(index);
			if (counts.get(label).decrementAndGet() <= 0)
				counts.remove(label);
			labels.remove(index);
			values.remove(index);
		}

		public void forEach(final BiConsumer<String, String> fn) {
			for (int i = 0; i < labels.size(); ++i) {
				final String label;
				final int dup = dups.get(i);
				if (dup == 0) {
					label = labels.get(i);
				} else {
					label = labels.get(i) + "_" + dup;
				}
				fn.accept(label, values.get(i));
			}
		}

		public int size() {
			return labels.size();
		}
	}

	private static void unfoldArray(final List<String> nameKeys, final Labels labels, final List<String> names, final Object arrayValue, final String arrayType, final AttributeValue input, final MetricValueOutput output) {
		final String elementType = MoreClasses.elementTypeNameOf(arrayType);
		int length = Array.getLength(arrayValue);
		for (int i = 0; i < length; ++i) {
			final Object element = Array.get(arrayValue, i);
			names.add(null);
			labels.push("index", String.valueOf(i));
			unfold(nameKeys, labels, names, element, elementType, input, output);
			labels.pop();
			names.remove(names.size() - 1);
		}
	}

	private static void unfoldCompositeData(final List<String> nameKeys, final Labels labels, final List<String> names, final CompositeData compositeData, final AttributeValue input, final MetricValueOutput output) {
		final CompositeType compositeType = compositeData.getCompositeType();
		for (final String key : compositeType.keySet()) {
			names.add(key);
			unfold(nameKeys, labels, names, compositeData.get(key), compositeType.getType(key).getClassName(), input, output);
			names.remove(names.size() - 1);
		}
	}

	private static void unfoldTabularData(final List<String> nameKeys, final Labels labels, final List<String> names, final TabularData tabularData, final AttributeValue input, final MetricValueOutput output) {
		final TabularType tabularType = tabularData.getTabularType();
		final CompositeType rowType = tabularType.getRowType();
		final List<String> indexColumnNames = tabularType.getIndexNames();
		final List<String> nonIndexColumnNames = new ArrayList<>();
		for (final String columnName : rowType.keySet()) {
			if (indexColumnNames.contains(columnName))
				continue;
			nonIndexColumnNames.add(columnName);
		}

		@SuppressWarnings("unchecked")
		final Collection<CompositeData> rows = (Collection<CompositeData>) tabularData.values();
		for (final CompositeData row : rows) {
			for (final String indexColumnName : indexColumnNames) {
				// TODO: proper string conversion
				labels.push(indexColumnName, String.valueOf(row.get(indexColumnName)));
			}

			for (final String columnName : nonIndexColumnNames) {
				names.add(columnName);
				unfold(nameKeys, labels, names, row.get(columnName), rowType.getType(columnName).getClassName(), input, output);
				names.remove(names.size() - 1);
			}

			for (int i = 0; i < indexColumnNames.size(); ++i) {
				labels.pop();
			}
		}
	}

	private static void unfoldByDynamicType(final List<String> nameKeys, final Labels labels, final List<String> names, final Object value, final String type, final AttributeValue input, final MetricValueOutput output) {
		if (value instanceof Number) {
			emit(nameKeys, labels, names, input, output, ((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			emit(nameKeys, labels, names, input, output, (Boolean) value ? 1 : 0);
		} else if (value instanceof CompositeData) {
			unfoldCompositeData(nameKeys, labels, names, (CompositeData) value, input, output);
		} else if (value instanceof TabularData) {
			unfoldTabularData(nameKeys, labels, names, (TabularData) value, input, output);
		} else {
			if (SUPPRESSED_TYPES.add(new ObjectAndAttributeName(input.domain, input.keyProperties, input.attributeName)))
				LOG.warning(String.format("Got unsupported dynamic type \"%s\" while processing %s:%s. Further warnings are suppressed for the attribute.", type, formatObjectNameForLogging(input.domain, input.keyProperties), input.attributeName));
		}
		// TODO: handle arrays, characters, strings and ObjectName
	}

	/**
	 * @param nameKeys
	 * @param labels
	 * @param names
	 * @param value
	 * @param type     a string representation of the type of the value. The format is the same as {@link Class#getName()}.
	 * @param input
	 * @param output
	 */
	private static void unfold(final List<String> nameKeys, final Labels labels, final List<String> names, final Object value, final String type, final AttributeValue input, final MetricValueOutput output) {
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
			emit(nameKeys, labels, names, input, output, ((Number) value).doubleValue());
			break;
		case "boolean": /* fall through */
		case "java.lang.Boolean":
			if (value == null)
				break;
			emit(nameKeys, labels, names, input, output, (Boolean) value ? 1 : 0);
			break;
		case "javax.management.ObjectName": /* fall through */
		case "java.lang.String":
			// Prometheus doesn't support textual values. Skip.
			break;
		case "char": /* fall through */
		case "java.lang.Character":
			if (value == null)
				break;
			emit(nameKeys, labels, names, input, output, ((Character) value).charValue());
			break;
		case "javax.management.openmbean.CompositeData":
			if (value == null) // we can't reliably determine the details of the CompositeData type.
				break;
			unfoldCompositeData(nameKeys, labels, names, (CompositeData) value, input, output);
			break;
		case "javax.management.openmbean.TabularData":
			if (value == null)
				break;
			unfoldTabularData(nameKeys, labels, names, (TabularData) value, input, output);
			break;
		case "java.lang.Object":
			// If the reported type is java.lang.Object, let's try to guess from the actual value.
			if (value == null)
				break;
			unfoldByDynamicType(nameKeys, labels, names, value, type, input, output);
			break;
		default:
			if (type.startsWith("[")) { // array type
				if (value == null)
					break;
				unfoldArray(nameKeys, labels, names, value, type, input, output);
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

	private static void emit(final List<String> nameKeys, final Labels labels, final List<String> names, final AttributeValue input, final MetricValueOutput output, final double value) {
		final Map<String, String> metricLabels = Maps.newHashMapWithExpectedSize(labels.size() + input.keyProperties.size());
		labels.forEach((label, labelValue) -> {
			metricLabels.put(label, labelValue);
		});
		input.keyProperties.forEach((k, v) -> metricLabels.put(k, v));

		final StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(input.domain);
		for (final String nameKey : nameKeys) {
			final String metricLabelValue = metricLabels.get(nameKey);
			if (metricLabelValue == null)
				continue;
			nameBuilder.append(":");
			nameBuilder.append(metricLabelValue);
		}
		for (final String nameKey : nameKeys) {
			metricLabels.remove(nameKey);
		}

		final StringBuilder attributeNameBuilder = new StringBuilder();
		attributeNameBuilder.append(input.attributeName);
		for (final String name : names) {
			if (name != null) {
				attributeNameBuilder.append("_");
				attributeNameBuilder.append(name);
			}
		}

		nameBuilder.append(":");
		nameBuilder.append(attributeNameBuilder);

		final MetricValue m = new MetricValue();
		m.name = nameBuilder.toString();
		m.value = value;
		m.labels = metricLabels;
		m.timestamp = input.timestamp;
		m.help = input.attributeDescription;
		output.emit(m);
	}

	public static void transformV1(final AttributeValue sample, final MetricValueOutput output, final String... propertiesToUseInMetricName) {
		final Labels labels = new Labels();
		final List<String> names = new ArrayList<>();
		unfold(Arrays.asList(propertiesToUseInMetricName), labels, names, sample.value, sample.attributeType, sample, output);
	}
}
