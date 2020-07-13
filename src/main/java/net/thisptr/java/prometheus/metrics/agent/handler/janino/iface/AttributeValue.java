package net.thisptr.java.prometheus.metrics.agent.handler.janino.iface;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

public class AttributeValue {
	public ObjectName name;
	public MBeanInfo mbeanInfo;
	public MBeanAttributeInfo attributeInfo;

	public long timestamp;
	public Object value;
}