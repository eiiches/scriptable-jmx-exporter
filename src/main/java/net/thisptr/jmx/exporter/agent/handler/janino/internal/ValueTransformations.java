package net.thisptr.jmx.exporter.agent.handler.janino.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import com.fasterxml.jackson.databind.node.TextNode;

import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.utils.MoreClasses;

public class ValueTransformations {
	private static final Logger LOG = Logger.getLogger(ValueTransformations.class.getName());

	private static class NameAndLabels {
		private final String name;
		private final Map<String, String> labels;

		public NameAndLabels(final String name, final Map<String, String> labels) {
			this.name = name;
			this.labels = labels;
		}

		public static NameAndLabels from(final MetricNamer namer, final Labels labels) {
			final String currentName = namer.toString();
			final Map<String, String> currentLabels = new HashMap<>();
			labels.forEach(currentLabels::put);
			return new NameAndLabels(currentName, currentLabels);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((labels == null) ? 0 : labels.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final NameAndLabels other = (NameAndLabels) obj;
			if (labels == null) {
				if (other.labels != null)
					return false;
			} else if (!labels.equals(other.labels))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		/**
		 * For debugging.
		 */
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append(name);
			builder.append('{');
			labels.forEach((k, v) -> {
				builder.append(k);
				builder.append('=');
				builder.append(TextNode.valueOf(v).toString());
				builder.append(',');
			});
			builder.append('}');
			return builder.toString();
		}
	}

	private static final Set<NameAndLabels> SUPPRESSED_TYPES = Collections.newSetFromMap(new ConcurrentHashMap<NameAndLabels, Boolean>());

	private static void unfoldArray(final MetricNamer namer, final Labels labels, final Object arrayValue, final String arrayType, final MetricValueOutput output) {
		final String elementType = MoreClasses.elementTypeNameOf(arrayType);
		int length = Array.getLength(arrayValue);
		for (int i = 0; i < length; ++i) {
			final Object element = Array.get(arrayValue, i);
			labels.push("index", String.valueOf(i));
			unfold(namer, labels, element, elementType, output);
			labels.pop("index");
		}
	}

	private static void unfoldListType(final MetricNamer namer, final Labels labels, final List<Object> list, final MetricValueOutput output) {
		int length = list.size();
		for (int i = 0; i < length; ++i) {
			final Object element = list.get(i);
			labels.push("index", String.valueOf(i));
			unfoldByDynamicType(namer, labels, element, output);
			labels.pop("index");
		}
	}

	private static void unfoldCompositeData(final MetricNamer namer, final Labels labels, final CompositeData compositeData, final MetricValueOutput output) {
		final CompositeType compositeType = compositeData.getCompositeType();
		for (final String key : compositeType.keySet()) {
			final int mark = namer.push(key);
			unfold(namer, labels, compositeData.get(key), compositeType.getType(key).getClassName(), output);
			namer.pop(mark);
		}
	}

	private static void unfoldTabularData(final MetricNamer namer, final Labels labels, final TabularData tabularData, final MetricValueOutput output) {
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
				unfold(namer, labels, row.get(columnName), columnType, output);
				namer.pop(mark);
			}

			for (int i = indexColumnNames.size() - 1; i >= 0; --i) { // reverse order
				labels.pop(indexColumnNames.get(i));
			}
		}
	}

	private static void unfoldMapType(final MetricNamer namer, final Labels labels, final Map<Object, Object> map, final MetricValueOutput output) {
		map.forEach((k, v) -> {
			labels.push("key", k.toString());
			unfoldByDynamicType(namer, labels, v, output);
			labels.pop("key");
		});
	}

	private static void unfoldByDynamicType(final MetricNamer namer, final Labels labels, final Object value, final MetricValueOutput output) {
		if (value == null)
			return;
		if (value instanceof Number) {
			emit(namer, labels, output, ((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			emit(namer, labels, output, (Boolean) value ? 1 : 0);
		} else if (value instanceof CompositeData) {
			unfoldCompositeData(namer, labels, (CompositeData) value, output);
		} else if (value instanceof TabularData) {
			unfoldTabularData(namer, labels, (TabularData) value, output);
		} else {
			final NameAndLabels current = NameAndLabels.from(namer, labels);
			if (SUPPRESSED_TYPES.add(current))
				LOG.warning(String.format("Got unsupported dynamic type \"%s\" while processing %s. Further warnings are suppressed for the this metric.", value.getClass().getName(), current));
		}
		// TODO: handle arrays, characters, strings and ObjectName
	}

	/**
	 * @param namer
	 * @param labels
	 * @param value
	 * @param type   a string representation of the type of the value. The format is the same as {@link Class#getName()}.
	 * @param output
	 */
	public static void unfold(final MetricNamer namer, final Labels labels, final Object value, final String type, final MetricValueOutput output) {
		if (type == null) {
			// Normally, this doesn't happen, but I came across this when I was using Tomcat. The type of
			// "Users:type=UserDatabase,database=UserDatabase:writeable" attribute was null. While this is something
			// that should be fixed in Tomcat, we could handle it here by inspecting the value itself.
			if (value == null)
				return;
			unfoldByDynamicType(namer, labels, value, output);
			return;
		}
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
			emit(namer, labels, output, ((Number) value).doubleValue());
			break;
		case "boolean": /* fall through */
		case "java.lang.Boolean":
			if (value == null)
				break;
			emit(namer, labels, output, (Boolean) value ? 1 : 0);
			break;
		case "javax.management.ObjectName": /* fall through */
		case "java.lang.String":
			// Prometheus doesn't support textual values. Skip.
			break;
		case "char": /* fall through */
		case "java.lang.Character":
			if (value == null)
				break;
			emit(namer, labels, output, ((Character) value).charValue());
			break;
		case "javax.management.openmbean.CompositeData":
			if (value == null) // we can't reliably determine the details of the CompositeData type.
				break;
			unfoldCompositeData(namer, labels, (CompositeData) value, output);
			break;
		case "javax.management.openmbean.TabularData":
			if (value == null)
				break;
			unfoldTabularData(namer, labels, (TabularData) value, output);
			break;
		case "java.lang.Object":
			// If the reported type is java.lang.Object, let's try to guess from the actual value.
			if (value == null)
				break;
			unfoldByDynamicType(namer, labels, value, output);
			break;
		case "java.util.Map":
			if (value == null)
				break;
			@SuppressWarnings("unchecked")
			final Map<Object, Object> mapValue = (Map<Object, Object>) value;
			unfoldMapType(namer, labels, mapValue, output);
			break;
		case "java.util.List":
			if (value == null)
				break;
			@SuppressWarnings("unchecked")
			final List<Object> listValue = (List<Object>) value;
			unfoldListType(namer, labels, listValue, output);
			break;
		default:
			if (type.startsWith("[")) { // array type
				if (value == null)
					break;
				unfoldArray(namer, labels, value, type, output);
			} else {
				final NameAndLabels current = NameAndLabels.from(namer, labels);
				if (SUPPRESSED_TYPES.add(current))
					LOG.warning(String.format("Got unsupported type \"%s\" while processing %s. Further warnings are suppressed for the this metric.", type, current));
				break;
			}
		}
	}

	private static void emit(final MetricNamer namer, final Labels labels, final MetricValueOutput output, final double value) {
		final MetricValue m = new MetricValue();
		m.name = namer.toString();
		m.value = value;
		m.labels = labels.toMapIfNotEmpty();
		output.emit(m);
	}
}
