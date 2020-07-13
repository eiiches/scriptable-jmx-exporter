package net.thisptr.java.prometheus.metrics.agent.handler.jq;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Maps;

import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.jackson.JmxModule;
import net.thisptr.java.prometheus.metrics.agent.misc.Converter;

public class SampleToJsonInputConverter implements Converter<Sample<?>, JsonNode> {
	private static final SampleToJsonInputConverter INSTANCE = new SampleToJsonInputConverter();

	public static SampleToJsonInputConverter getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonNode convert(final Sample<?> t) {
		return toJsonNode(t);
	}

	public static final ObjectMapper JMX_MAPPER = new ObjectMapper()
			.registerModule(new JmxModule())
			.disable(MapperFeature.AUTO_DETECT_GETTERS)
			.disable(MapperFeature.AUTO_DETECT_FIELDS)
			.disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
			.disable(MapperFeature.AUTO_DETECT_SETTERS)
			.disable(MapperFeature.AUTO_DETECT_CREATORS);

	private static JsonNode serializeCompositeData(final CompositeData data, final CompositeType type) {
		final Set<String> keys = type.keySet();
		final Map<String, JsonNode> root = Maps.newHashMapWithExpectedSize(keys.size() + 1);
		root.put("$type", TextNode.valueOf("javax.management.openmbean.CompositeData"));
		for (final String key : keys)
			root.put(key, serialize(data.get(key)));
		return new ObjectNode(JMX_MAPPER.getNodeFactory(), root);
	}

	private static JsonNode serializeCompositeData(final CompositeData data) {
		final CompositeType type = data.getCompositeType();
		return serializeCompositeData(data, type);
	}

	private static JsonNode serializeTabularData(final TabularData data) {
		final TabularType type = data.getTabularType();
		final CompositeType rowType = type.getRowType();

		final Map<String, JsonNode> root = Maps.newHashMapWithExpectedSize(3);
		root.put("$type", TextNode.valueOf("javax.management.openmbean.TabularData"));

		final Collection<?> values = data.values();
		final List<JsonNode> jsonValues = new ArrayList<>(values.size());
		for (final Object value : values)
			jsonValues.add(serializeCompositeData((CompositeData) value, rowType));
		root.put("values", new ArrayNode(JMX_MAPPER.getNodeFactory(), jsonValues));

		final List<String> indexNames = type.getIndexNames();
		final List<JsonNode> jsonIndexNames = new ArrayList<>(indexNames.size());
		for (final String indexName : indexNames)
			jsonIndexNames.add(TextNode.valueOf(indexName));
		final Map<String, JsonNode> jsonTabularType = Maps.newHashMapWithExpectedSize(1);
		jsonTabularType.put("index_names", new ArrayNode(JMX_MAPPER.getNodeFactory(), jsonIndexNames));
		root.put("tabular_type", new ObjectNode(JMX_MAPPER.getNodeFactory(), jsonTabularType));

		return new ObjectNode(JMX_MAPPER.getNodeFactory(), root);
	}

	private static JsonNode serializeObjectName(final ObjectName name) {
		return TextNode.valueOf(name.toString());
	}

	private static JsonNode serialize(final Object value) {
		if (value == null)
			return NullNode.getInstance();
		if (value instanceof Long) {
			return LongNode.valueOf((Long) value);
		} else if (value instanceof Integer) {
			return IntNode.valueOf((Integer) value);
		} else if (value instanceof Double) {
			return DoubleNode.valueOf((Double) value);
		} else if (value instanceof Float) {
			return FloatNode.valueOf((Float) value);
		} else if (value instanceof Boolean) {
			return BooleanNode.valueOf((Boolean) value);
		} else if (value instanceof String) {
			return TextNode.valueOf((String) value);
		} else if (value instanceof CompositeData) {
			return serializeCompositeData((CompositeData) value);
		} else if (value instanceof TabularData) {
			return serializeTabularData((TabularData) value);
		} else if (value instanceof ObjectName) {
			return serializeObjectName((ObjectName) value);
		} else if (value.getClass().isArray()) {
			int length = Array.getLength(value);
			final List<JsonNode> root = new ArrayList<>(length);
			for (int i = 0; i < length; ++i)
				root.add(serialize(Array.get(value, i)));
			return new ArrayNode(JMX_MAPPER.getNodeFactory(), root);
		} else {
			// resort to jackson serialization
			return JMX_MAPPER.valueToTree(value);
		}
	}

	private static JsonNode toJsonNode(final Sample<?> sample) {
		final JsonNode valueJson = serialize(sample.value);

		final Map<String, JsonNode> out = Maps.newHashMapWithExpectedSize(6);
		out.put("type", TextNode.valueOf(sample.attribute.getType()));
		out.put("mbean_description", TextNode.valueOf(sample.info.getDescription()));
		out.put("value", valueJson != null ? valueJson : NullNode.getInstance());
		out.put("domain", TextNode.valueOf(sample.name.getDomain()));
		final Hashtable<String, String> propertyList = sample.name.getKeyPropertyList();
		final Map<String, JsonNode> properties = Maps.newHashMapWithExpectedSize(propertyList.size());
		propertyList.forEach((k, v) -> {
			if (v.startsWith("\""))
				v = ObjectName.unquote(v);
			properties.put(k, TextNode.valueOf(v));
		});
		out.put("description", TextNode.valueOf(sample.attribute.getDescription()));
		out.put("properties", new ObjectNode(JMX_MAPPER.getNodeFactory(), properties));
		out.put("attribute", TextNode.valueOf(sample.attribute.getName()));
		out.put("timestamp", LongNode.valueOf(sample.timestamp));
		return new ObjectNode(JMX_MAPPER.getNodeFactory(), out);
	}
}
