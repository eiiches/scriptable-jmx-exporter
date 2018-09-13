package net.thisptr.java.prometheus.metrics.agent.scraper;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.java.prometheus.metrics.agent.jackson.JmxModule;
import net.thisptr.java.prometheus.metrics.agent.misc.AttributeNamePattern;

public class Scraper<ScrapeRuleType extends ScrapeRule> {
	private static final Logger LOG = Logger.getLogger(Scraper.class.getName());

	public static final ObjectMapper JMX_MAPPER = new ObjectMapper()
			.registerModule(new JmxModule())
			.disable(MapperFeature.AUTO_DETECT_GETTERS)
			.disable(MapperFeature.AUTO_DETECT_FIELDS)
			.disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
			.disable(MapperFeature.AUTO_DETECT_SETTERS)
			.disable(MapperFeature.AUTO_DETECT_CREATORS);

	private final List<ScrapeRuleType> rules;
	private final MBeanServer server;

	public Scraper(final MBeanServer server, final List<ScrapeRuleType> rules) {
		this.server = server;
		this.rules = rules;
	}

	private ScrapeRuleType findRule(final ObjectName name, final String attribute) {
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty())
				return rule;
			for (final AttributeNamePattern pattern : rule.patterns())
				if (pattern.matches(name, attribute))
					return rule;
		}
		return null;
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output) throws IntrospectionException, InstanceNotFoundException, ReflectionException, IOException {
		for (final ObjectInstance instance : server.queryMBeans(null, null)) {
			try {
				scrape(instance, output);
			} catch (final Throwable th) {
				LOG.log(Level.FINE, "Failed to scrape MBean instance (" + instance.getObjectName() + ").", th);
			}
		}
	}

	public void scrape(final ObjectInstance instance, final ScrapeOutput<ScrapeRuleType> output) throws IntrospectionException, InstanceNotFoundException, ReflectionException {
		final ObjectName name = instance.getObjectName();
		for (final MBeanAttributeInfo attribute : server.getMBeanInfo(name).getAttributes()) {
			try {
				scrape(instance, attribute, output);
			} catch (final Throwable th) {
				LOG.log(Level.FINER, "Failed to scrape MBean attribute (" + name + ":" + attribute.getName() + ").", th);
			}
		}
	}

	public void scrape(final ObjectInstance instance, final MBeanAttributeInfo attribute, final ScrapeOutput<ScrapeRuleType> output) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, JsonQueryException, JsonProcessingException, IOException {
		if (!attribute.isReadable())
			return;

		final ObjectName name = instance.getObjectName();
		final ScrapeRuleType rule = findRule(name, attribute.getName());
		if (rule != null && rule.skip())
			return;

		final long timestamp = System.currentTimeMillis();
		final Object value;
		try {
			value = server.getAttribute(name, attribute.getName());
		} catch (final RuntimeMBeanException e) {
			if (e.getCause() instanceof UnsupportedOperationException)
				return;
			throw e;
		}

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

		output.emit(rule, timestamp, out);
	}
}
