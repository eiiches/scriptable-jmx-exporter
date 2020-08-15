package net.thisptr.jmx.exporter.agent.scripting;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

public interface ConditionScript {
	boolean evaluate(final MBeanInfo bean, final MBeanAttributeInfo attribute);
}
