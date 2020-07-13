package net.thisptr.java.prometheus.metrics.agent.handler.janino.functions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import com.google.common.collect.Maps;

import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.AttributeValue;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.MetricValue;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.MetricValueOutput;
import net.thisptr.java.prometheus.metrics.agent.misc.MutableInteger;
import net.thisptr.java.prometheus.metrics.agent.utils.MoreClasses;

public class TransformV1Function {
	private static final Logger LOG = Logger.getLogger(TransformV1Function.class.getName());

	private static final Set<String> SUPPRESSED_TYPES = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

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
			emit(nameKeys, labels, names, input, output, value != null ? ((Number) value).doubleValue() : Double.NaN);
			break;
		case "boolean": /* fall through */
		case "java.lang.Boolean":
			emit(nameKeys, labels, names, input, output, value != null ? ((Boolean) value ? 1 : 0) : Double.NaN);
			break;
		case "javax.management.ObjectName": /* fall through */
		case "java.lang.String":
			// Prometheus doesn't support textual values. Skip.
			break;
		case "char": /* fall through */
		case "java.lang.Character":
			emit(nameKeys, labels, names, input, output, value != null ? ((Character) value).charValue() : Double.NaN);
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
		default:
			if (type.startsWith("[")) { // array type
				if (value == null)
					break;
				unfoldArray(nameKeys, labels, names, value, type, input, output);
			} else {
				if (SUPPRESSED_TYPES.add(type))
					LOG.warning(String.format("Got unsupported type \"%s\" while processing %s:%s. Further warnings are suppressed for the type.", type, input.name, input.attributeInfo.getName()));
				break;
			}
		}
	}

	private static void emit(final List<String> nameKeys, final Labels labels, final List<String> names, final AttributeValue input, final MetricValueOutput output, final double value) {
		// TODO: Move this somewhere so that we don't have to allocate the hash table on every invocation.
		final Hashtable<String, String> keyProperties = input.name.getKeyPropertyList();

		final Map<String, String> metricLabels = Maps.newHashMapWithExpectedSize(labels.size() + keyProperties.size());
		labels.forEach((label, labelValue) -> {
			metricLabels.put(label, labelValue);
		});
		keyProperties.forEach((k, v) -> metricLabels.put(k, v));

		final StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(input.name.getDomain());
		for (final String nameKey : nameKeys) {
			nameBuilder.append(":");
			nameBuilder.append(metricLabels.get(nameKey));
		}

		final StringBuilder attributeNameBuilder = new StringBuilder();
		attributeNameBuilder.append(input.attributeInfo.getName());
		for (final String name : names) {
			if (name != null) {
				attributeNameBuilder.append("_");
				attributeNameBuilder.append(name);
			}
		}

		for (final String nameKey : nameKeys) {
			metricLabels.remove(nameKey);
		}

		nameBuilder.append(":");
		nameBuilder.append(attributeNameBuilder);

		final MetricValue m = new MetricValue();
		m.name = nameBuilder.toString();
		m.value = value;
		m.labels = metricLabels;
		m.timestamp = input.timestamp;
		m.help = input.attributeInfo.getDescription();
		output.emit(m);
	}

	public static void transformV1(final AttributeValue sample, final MetricValueOutput output, final String... propertiesToUseInMetricName) {
		final Labels labels = new Labels();
		final List<String> names = new ArrayList<>();
		unfold(Arrays.asList(propertiesToUseInMetricName), labels, names, sample.value, sample.attributeInfo.getType(), sample, output);
	}
}
