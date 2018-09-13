package net.thisptr.java.prometheus.metrics.agent.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

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

	private void enumerate(final BiConsumer<ObjectInstance, MBeanAttributeInfo> output) {
		try {
			for (final ObjectInstance instance : server.queryMBeans(null, null))
				enumerate(instance, output);
		} catch (final Throwable th) {
			LOG.log(Level.FINE, "Failed to enumerate MBean instances.", th);
		}
	}

	private void enumerate(final ObjectInstance instance, final BiConsumer<ObjectInstance, MBeanAttributeInfo> output) {
		final ObjectName name = instance.getObjectName();
		try {
			for (final MBeanAttributeInfo attribute : server.getMBeanInfo(name).getAttributes()) {
				try {
					output.accept(instance, attribute);
				} catch (final Throwable th) {
					LOG.log(Level.WARNING, "Got unexpected exception from callback (name = " + name + ", attribute = " + attribute.getName() + ").", th);
				}
			}
		} catch (final Throwable th) {
			LOG.log(Level.FINER, "Failed to enumerate attributes of the MBean instance (name = " + name + ")", th);
		}
	}

	private class AttributeScrapeRequest {
		public final ObjectInstance instance;
		public final MBeanAttributeInfo attribute;
		public final ScrapeRuleType rule;

		public AttributeScrapeRequest(final ObjectInstance instance, final MBeanAttributeInfo attribute, final ScrapeRuleType rule) {
			this.instance = instance;
			this.attribute = attribute;
			this.rule = rule;
		}
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output) throws InterruptedException {
		scrape(output, 0L, TimeUnit.MILLISECONDS);
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output, final long duration, final TimeUnit unit) throws InterruptedException {
		final List<AttributeScrapeRequest> requests = new ArrayList<>();
		enumerate((instance, attribute) -> {
			final ObjectName name = instance.getObjectName();
			if (!attribute.isReadable())
				return;
			final ScrapeRuleType rule = findRule(name, attribute.getName());
			if (rule != null && rule.skip())
				return;
			requests.add(new AttributeScrapeRequest(instance, attribute, rule));
		});

		final long startNanos = System.nanoTime();
		final long durationNanos = unit.toNanos(duration);
		for (int i = 0; i < requests.size(); ++i) {
			final long waitUntilNanos = startNanos + (long) (((i + 1) / (double) requests.size()) * durationNanos);
			final long sleepNanos = waitUntilNanos - System.nanoTime();
			if (sleepNanos > 0)
				sleepNanos(sleepNanos);
			final AttributeScrapeRequest request = requests.get(i);
			try {
				scrape(request.instance, request.attribute, request.rule, output);
			} catch (final Throwable th) {
				LOG.log(Level.FINER, "Failed to scrape the attribute of the MBean instance (name = " + request.instance.getObjectName() + ", attribute = " + request.attribute.getName() + ")", th);
			}
		}
	}

	private static void sleepNanos(final long totalNanos) throws InterruptedException {
		final int nanos = (int) (totalNanos % 1000000L);
		final long millis = totalNanos / 1000000L;
		Thread.sleep(millis, nanos);
	}

	private void scrape(final ObjectInstance instance, final MBeanAttributeInfo attribute, final ScrapeRuleType rule, final ScrapeOutput<ScrapeRuleType> output) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
		final ObjectName name = instance.getObjectName();

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
