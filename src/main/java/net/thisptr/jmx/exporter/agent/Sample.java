package net.thisptr.jmx.exporter.agent;

import java.util.Map;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import net.thisptr.jmx.exporter.agent.config.Config.ScrapeRule;
import net.thisptr.jmx.exporter.agent.misc.FastObjectName;

public class Sample {

	public final ScrapeRule rule;
	public final long timestamp;
	public final MBeanAttributeInfo attribute;
	public final Object value;
	public final FastObjectName name;
	public final MBeanInfo info;
	public final Map<String, String> captures;

	public Sample(final ScrapeRule rule, final Map<String, String> captures, final long timestamp, final FastObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final Object value) {
		this.rule = rule;
		this.captures = captures;
		this.timestamp = timestamp;
		this.name = name;
		this.info = info;
		this.attribute = attribute;
		this.value = value;
	}
}