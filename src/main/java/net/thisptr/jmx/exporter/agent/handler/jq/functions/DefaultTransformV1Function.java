package net.thisptr.jmx.exporter.agent.handler.jq.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Maps;

import net.thisptr.jackson.jq.Expression;
import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.PathOutput;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Version;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.path.Path;

public class DefaultTransformV1Function implements Function {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public void apply(final Scope scope, final List<Expression> args, final JsonNode in, final Path path, final PathOutput output, final Version version) throws JsonQueryException {
		if (args.size() == 2) {
			final Expression nameKeysExpr = args.get(0);
			final Expression attrAsNameExpr = args.get(1);

			nameKeysExpr.apply(scope, in, path, (nameKeysJson, nameKeysPath) -> {
				final List<String> nameKeys = new ArrayList<>(nameKeysJson.size());
				for (final JsonNode nameKeyJson : nameKeysJson)
					nameKeys.add(nameKeyJson.asText());

				attrAsNameExpr.apply(scope, in, path, (attrAsNameJson, attrAsNamePath) -> {
					final boolean attrAsName = attrAsNameJson.asBoolean();
					transform(nameKeys, attrAsName, in, output);
				}, false);
			}, false);
		} else {
			transform(Collections.emptyList(), false, in, output);
		}
	}

	private static void unfold(final List<String> nameKeys, final boolean attrAsName, final JsonSampleInput sample, final PathOutput output) {
		final List<IndexLabel> labels = new ArrayList<>();
		final List<String> names = new ArrayList<>();
		unfold(nameKeys, attrAsName, labels, names, sample.value, sample, output);
	}

	private static String format(final List<String> names) {
		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (final String name : names) {
			builder.append(sep);
			if (name == null) {
				builder.append("index");
			} else {
				builder.append(name);
			}
			sep = "_";
		}
		return builder.toString();
	}

	private static class IndexLabel {
		public final String label;
		public final String index;

		public IndexLabel(final String label, final String index) {
			this.label = label;
			this.index = index;
		}
	}

	private static void unfold(final List<String> nameKeys, final boolean attrAsName, final List<IndexLabel> labels, final List<String> names, final JsonNode value, final JsonSampleInput sample, final PathOutput output) {
		switch (value.getNodeType()) {
		case ARRAY: {
			final Iterator<JsonNode> iter = value.iterator();
			for (int index = 1; iter.hasNext(); ++index) {
				final JsonNode item = iter.next();
				names.add(null);
				labels.add(new IndexLabel(format(names), String.valueOf(index)));
				unfold(nameKeys, attrAsName, labels, names, item, sample, output);
				labels.remove(labels.size() - 1);
				names.remove(names.size() - 1);
			}
			break;
		}
		case OBJECT: {
			final JsonNode type = value.get("$type");
			if (type == null)
				break; // skip
			switch (type.asText()) {
			case "javax.management.openmbean.CompositeData": {
				final Iterator<Entry<String, JsonNode>> iter = value.fields();
				while (iter.hasNext()) {
					final Entry<String, JsonNode> entry = iter.next();
					if ("$type".equals(entry.getKey()))
						continue;
					names.add(entry.getKey());
					unfold(nameKeys, attrAsName, labels, names, entry.getValue(), sample, output);
					names.remove(names.size() - 1);
				}
				break;
			}
			case "javax.management.openmbean.TabularData":
				final List<String> indexNames = new ArrayList<>();
				value.get("tabular_type").get("index_names").forEach((indexName) -> {
					indexNames.add(indexName.asText());
				});
				for (final JsonNode tabularRecord : value.get("values")) {
					for (final String indexName : indexNames) {
						names.add(indexName);
						final JsonNode labelValue = tabularRecord.get(indexName);
						labels.add(new IndexLabel(format(names), labelValue.isTextual() ? labelValue.asText() : labelValue.toString()));
						names.remove(names.size() - 1);
					}

					final Iterator<Entry<String, JsonNode>> tabularFieldIterator = tabularRecord.fields();
					while (tabularFieldIterator.hasNext()) {
						final Entry<String, JsonNode> tabularField = tabularFieldIterator.next();
						if (indexNames.contains(tabularField.getKey())) // FIXME: O(N)
							continue;
						names.add(tabularField.getKey());
						unfold(nameKeys, attrAsName, labels, names, tabularField.getValue(), sample, output);
						names.remove(names.size() - 1);
					}

					for (int i = 0; i < indexNames.size(); ++i) {
						labels.remove(labels.size() - 1);
					}
				}
				break;
			default:
				break; // skip
			}
			break;
		}
		case NUMBER:
			emit(nameKeys, attrAsName, labels, names, sample, output, value.asDouble());
			break;
		case BOOLEAN:
			emit(nameKeys, attrAsName, labels, names, sample, output, value.asBoolean() ? 1.0 : 0.0);
			break;
		default:
			break; // skip
		}
	}

	private static void emit(final List<String> nameKeys, final boolean attrAsName, final List<IndexLabel> labels, final List<String> names, final JsonSampleInput sample, final PathOutput output, final double value) {
		try {
			final Map<String, JsonNode> metricLabels = Maps.newHashMapWithExpectedSize(labels.size() + sample.properties.size());
			labels.forEach((label) -> {
				metricLabels.put(label.label, TextNode.valueOf(label.index));
			});
			sample.properties.forEach(metricLabels::put);

			final StringBuilder nameBuilder = new StringBuilder();
			nameBuilder.append(sample.domain);
			for (final String nameKey : nameKeys) {
				nameBuilder.append(":");
				nameBuilder.append(metricLabels.get(nameKey).asText());
			}

			final StringBuilder attributeNameBuilder = new StringBuilder();
			attributeNameBuilder.append(sample.attribute);
			for (final String name : names) {
				if (name != null) {
					attributeNameBuilder.append("_");
					attributeNameBuilder.append(name);
				}
			}

			for (final String nameKey : nameKeys) {
				metricLabels.remove(nameKey);
			}

			if (attrAsName) {
				nameBuilder.append(":");
				nameBuilder.append(attributeNameBuilder);
			} else {
				metricLabels.put("attribute", TextNode.valueOf(attributeNameBuilder.toString()));
			}

			final Map<String, JsonNode> jsonOutput = Maps.newHashMapWithExpectedSize(3);
			jsonOutput.put("name", TextNode.valueOf(nameBuilder.toString()));
			jsonOutput.put("value", DoubleNode.valueOf(value));
			jsonOutput.put("labels", new ObjectNode(MAPPER.getNodeFactory(), metricLabels));
			if (sample.attributeDescription != null)
				jsonOutput.put("help", TextNode.valueOf(sample.attributeDescription));
			output.emit(new ObjectNode(MAPPER.getNodeFactory(), jsonOutput), null);
		} catch (final JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private static void transform(final List<String> nameKeys, final boolean attrAsName, final JsonNode in, final PathOutput output) throws JsonQueryException {
		// convert to Java object first to ease transformation.
		final JsonSampleInput value = JsonSampleInput.fromJsonNode(in);
		try {
			unfold(nameKeys, attrAsName, value, output);
		} catch (final Exception e) {
			throw new JsonQueryException(e);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class JsonSampleInput {
		@JsonProperty("type")
		public String type;

		@JsonProperty("value")
		public JsonNode value;

		@JsonProperty("domain")
		public String domain;

		@JsonProperty("properties")
		public Map<String, JsonNode> properties;

		@JsonProperty("timestamp")
		public long timestamp;

		@JsonProperty("attribute")
		public String attribute;

		@JsonProperty("mbean_description")
		public String mbeanDescription;

		@JsonProperty("description")
		public String attributeDescription;

		public static JsonSampleInput fromJsonNode(final JsonNode tree) {
			final JsonSampleInput sample = new JsonSampleInput();
			final JsonNode type = tree.get("type");
			sample.type = type != null ? type.asText() : null;
			sample.value = tree.get("value");
			sample.domain = tree.get("domain").asText();

			final JsonNode description = tree.get("description");
			sample.attributeDescription = description != null ? description.asText() : null;

			final JsonNode mbeanDescription = tree.get("mbean_description");
			sample.mbeanDescription = mbeanDescription != null ? mbeanDescription.asText() : null;

			final JsonNode properties = tree.get("properties");
			if (properties != null) {
				sample.properties = Maps.newHashMapWithExpectedSize(properties.size());
				final Iterator<Entry<String, JsonNode>> iter = properties.fields();
				while (iter.hasNext()) {
					final Entry<String, JsonNode> entry = iter.next();
					sample.properties.put(entry.getKey(), entry.getValue());
				}
			} else {
				sample.properties = null;
			}

			final JsonNode timestamp = tree.get("timestamp");
			sample.timestamp = timestamp != null ? timestamp.asLong() : 0L;
			sample.attribute = tree.get("attribute").asText();
			return sample;
		}
	}
}
