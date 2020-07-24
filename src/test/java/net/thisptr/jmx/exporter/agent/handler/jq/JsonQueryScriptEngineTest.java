package net.thisptr.jmx.exporter.agent.handler.jq;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.jmx.exporter.agent.misc.FastObjectName;

public class JsonQueryScriptEngineTest {
	private final JsonQueryScriptEngine sut = new JsonQueryScriptEngine();

	private static Sample<PrometheusScrapeRule> sample(final ObjectName objectName, final String attributeName) throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IntrospectionException {
		final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		final Object value = server.getAttribute(objectName, attributeName);
		final long timestamp = System.currentTimeMillis();
		final MBeanInfo mbeanInfo = server.getMBeanInfo(objectName);
		final MBeanAttributeInfo attributeInfo = Arrays.stream(mbeanInfo.getAttributes()).filter(a -> attributeName.equals(a.getName())).findFirst().get();
		return new Sample<PrometheusScrapeRule>(null, Collections.emptyMap(), timestamp, new FastObjectName(objectName), mbeanInfo, attributeInfo, value);
	}

	@Test
	void testSimple() throws Exception {
		final Sample<PrometheusScrapeRule> sample = sample(new ObjectName("java.lang:type=OperatingSystem"), "ProcessCpuLoad");

		final List<PrometheusMetric> metrics = new ArrayList<>();
		sut.compile("default_transform_v1([\"type\"]; true)").execute(sample, metrics::add);

		assertThat(metrics.size()).isEqualTo(1);
		assertThat(metrics.get(0).value).isEqualTo((Double) sample.value);
		assertThat(metrics.get(0).name).isEqualTo("java.lang:OperatingSystem:ProcessCpuLoad");
		assertThat(metrics.get(0).labels).isEmpty();
	}
}
