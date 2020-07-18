package net.thisptr.java.prometheus.metrics.agent;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import net.thisptr.java.prometheus.metrics.agent.misc.FastObjectName;
import net.thisptr.java.prometheus.metrics.agent.scraper.ScrapeRule;

public class Sample<ScrapeRuleType extends ScrapeRule> {

	public final ScrapeRuleType rule;
	public final long timestamp;
	public final MBeanAttributeInfo attribute;
	public final Object value;
	public final FastObjectName name;
	public final MBeanInfo info;

	public Sample(final ScrapeRuleType rule, final long timestamp, final FastObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final Object value) {
		this.rule = rule;
		this.timestamp = timestamp;
		this.name = name;
		this.info = info;
		this.attribute = attribute;
		this.value = value;
	}
}