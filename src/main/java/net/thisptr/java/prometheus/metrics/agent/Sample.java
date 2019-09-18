package net.thisptr.java.prometheus.metrics.agent;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import net.thisptr.java.prometheus.metrics.agent.jackson.JmxModule;
import net.thisptr.java.prometheus.metrics.agent.scraper.ScrapeRule;

public class Sample<ScrapeRuleType extends ScrapeRule> {

	public static final ObjectMapper JMX_MAPPER = new ObjectMapper()
			.registerModule(new JmxModule())
			.disable(MapperFeature.AUTO_DETECT_GETTERS)
			.disable(MapperFeature.AUTO_DETECT_FIELDS)
			.disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
			.disable(MapperFeature.AUTO_DETECT_SETTERS)
			.disable(MapperFeature.AUTO_DETECT_CREATORS);

	public final ScrapeRuleType rule;
	public final long timestamp;
	public final MBeanAttributeInfo attribute;
	public final Object value;
	public final ObjectName name;
	public final MBeanInfo info;

	public Sample(final ScrapeRuleType rule, final long timestamp, final ObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final Object value) {
		this.rule = rule;
		this.timestamp = timestamp;
		this.name = name;
		this.info = info;
		this.attribute = attribute;
		this.value = value;
	}

	public JsonNode toJsonNode() {
		final JsonNode valueJson = JMX_MAPPER.valueToTree(value);

		final ObjectNode out = JMX_MAPPER.createObjectNode();
		out.set("type", TextNode.valueOf(attribute.getType()));
		out.set("value", valueJson);
		out.set("domain", TextNode.valueOf(name.getDomain()));
		final ObjectNode properties = JMX_MAPPER.createObjectNode();
		name.getKeyPropertyList().forEach((k, v) -> {
			if (v.startsWith("\""))
				v = ObjectName.unquote(v);
			properties.set(k, TextNode.valueOf(v));
		});
		out.set("properties", properties);
		out.set("attribute", TextNode.valueOf(attribute.getName()));
		out.set("timestamp", LongNode.valueOf(timestamp));
		return out;
	}
}