package net.thisptr.jmx.exporter.agent.scripting.janino;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
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
import javax.management.openmbean.CompositeData;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.thisptr.jmx.exporter.agent.misc.FastObjectName;
import net.thisptr.jmx.exporter.agent.scraper.Sample;
import net.thisptr.jmx.exporter.agent.scripting.ConditionScript;
import net.thisptr.jmx.exporter.agent.scripting.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.scripting.TransformScript;

public class JaninoScriptEngineTest {
	private final JaninoScriptEngine sut = new JaninoScriptEngine();

	private static Sample sample(final ObjectName objectName, final String attributeName) throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IntrospectionException {
		final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		final Object value = server.getAttribute(objectName, attributeName);
		final long timestamp = System.currentTimeMillis();
		final MBeanInfo mbeanInfo = server.getMBeanInfo(objectName);
		final MBeanAttributeInfo attributeInfo = Arrays.stream(mbeanInfo.getAttributes()).filter(a -> attributeName.equals(a.getName())).findFirst().get();
		return new Sample(null, Collections.emptyMap(), timestamp, new FastObjectName(objectName), mbeanInfo, attributeInfo, value);
	}

	@Test
	void testSimple() throws Exception {
		final Sample sample = sample(new ObjectName("java.lang:type=OperatingSystem"), "ProcessCpuLoad");

		final List<PrometheusMetric> metrics = new ArrayList<>();
		sut.compileTransformScript(Collections.emptyList(), "V1.transform(in, out, \"type\")", 0).execute(sample, metrics::add);

		assertThat(metrics.size()).isEqualTo(1);
		assertThat(metrics.get(0).value).isEqualTo((Double) sample.value);
		assertThat(metrics.get(0).name).isEqualTo("java.lang_OperatingSystem_ProcessCpuLoad");
		assertThat(metrics.get(0).labels).isNullOrEmpty();
	}

	@Test
	void testNonExistentKeyProperty() throws Exception {
		final Sample sample = sample(new ObjectName("java.lang:type=OperatingSystem"), "ProcessCpuLoad");

		final List<PrometheusMetric> metrics = new ArrayList<>();
		sut.compileTransformScript(Collections.emptyList(), "V1.transform(in, out, \"non_existent_key\")", 0).execute(sample, metrics::add);

		assertThat(metrics.size()).isEqualTo(1);
		assertThat(metrics.get(0).value).isEqualTo((Double) sample.value);
		assertThat(metrics.get(0).name).isEqualTo("java.lang_ProcessCpuLoad");
		assertThat(metrics.get(0).labels).containsExactlyInAnyOrderEntriesOf(Collections.singletonMap("type", "OperatingSystem"));
	}

	@Test
	void testArray() throws Exception {
		final Sample sample = sample(new ObjectName("java.lang:type=Threading"), "AllThreadIds");

		final List<PrometheusMetric> metrics = new ArrayList<>();
		sut.compileTransformScript(Collections.emptyList(), "V1.transform(in, out, \"type\")", 0).execute(sample, metrics::add);

		assertThat(metrics.size()).isEqualTo(Array.getLength(sample.value));

		for (int i = 0; i < metrics.size(); ++i) {
			assertThat(metrics.get(i).name).isEqualTo("java.lang_Threading_AllThreadIds");
			assertThat(metrics.get(i).value).isEqualTo(((Number) Array.get(sample.value, i)).doubleValue());
			assertThat(metrics.get(i).labels.size()).isEqualTo(1);
			assertThat(metrics.get(i).labels.get("index")).isEqualTo(String.valueOf(i));
		}
	}

	@Test
	void testCompositeData() throws Exception {
		final Sample sample = sample(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");

		final List<PrometheusMetric> metrics = new ArrayList<>();
		sut.compileTransformScript(Collections.emptyList(), "V1.transform(in, out, \"type\")", 0).execute(sample, metrics::add);

		assertThat(metrics.size()).isEqualTo(4);

		assertThat(metrics.get(0).name).isEqualTo("java.lang_Memory_HeapMemoryUsage_committed");
		assertThat(metrics.get(0).value).isEqualTo(((Number) ((CompositeData) sample.value).get("committed")).doubleValue());
		assertThat(metrics.get(0).labels).isNullOrEmpty();

		assertThat(metrics.get(3).name).isEqualTo("java.lang_Memory_HeapMemoryUsage_used");
		assertThat(metrics.get(3).value).isEqualTo(((Number) ((CompositeData) sample.value).get("used")).doubleValue());
		assertThat(metrics.get(3).labels).isNullOrEmpty();
	}

	private static ObjectName waitForLastGcInfo() {
		final long start = System.currentTimeMillis();
		byte[] unused;
		long allocated = 0;
		while (true) {
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			for (final ObjectName name : server.queryNames(null, null)) {
				if (!name.getKeyProperty("type").equals("GarbageCollector"))
					continue;
				try {
					if (server.getAttribute(name, "LastGcInfo") == null)
						continue;
				} catch (InstanceNotFoundException | AttributeNotFoundException | MBeanException | ReflectionException e) {
					continue;
				}
				return name;
			}
			unused = new byte[1 * 1024 * 1024];
			allocated += unused.length;
			if (System.currentTimeMillis() > 5 * 1000 + start)
				break;
		}
		throw new IllegalStateException("Time is up. LastGcInfo is still null after 5 seconds (allocated " + allocated + " bytes).");
	}

	@Test
	@Disabled
	void testTabularData() throws Exception {
		// FIXME: implement

		final Sample sample = sample(waitForLastGcInfo(), "LastGcInfo");

		final List<PrometheusMetric> metrics = new ArrayList<>();
		sut.compileTransformScript(Collections.emptyList(), "V1.transform(in, out, \"type\")", 0).execute(sample, metrics::add);

		System.out.println(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(metrics));

		assertThat(metrics.size()).isEqualTo(4);
	}

	@Test
	@Disabled
	void testAll() throws Exception {
		waitForLastGcInfo();

		final List<Sample> samples = new ArrayList<>();

		final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		for (final ObjectName name : server.queryNames(null, null)) {
			for (final MBeanAttributeInfo attribute : server.getMBeanInfo(name).getAttributes()) {
				try {
					samples.add(sample(name, attribute.getName()));
				} catch (Exception e) {
					continue;
				}
			}
		}

		final JaninoScriptEngine sut1 = new JaninoScriptEngine();
		// final JsonQueryScriptEngine sut2 = new JsonQueryScriptEngine();
		final TransformScript script1 = sut1.compileTransformScript(Collections.emptyList(), "V1.transform(in, out, \"type\")", 0);
		// final Script<?> script2 = sut2.compile("default_transform_v1([\"type\"]; true)");

		final long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; ++i) {
			for (final Sample sample : samples)
				script1.execute(sample, (a) -> {});
		}
		final long took = System.currentTimeMillis() - start;
		System.out.printf("total %d millsi\n", took);
	}

	@Test
	void testConditionScriptEqualsAndHashCode() throws Exception {
		final ConditionScript c = sut.compileConditionScript(Collections.emptyList(), "true", 0);

		final ConditionScript eq = sut.compileConditionScript(Collections.emptyList(), "true", 0);
		assertThat(c).isEqualTo(eq);
		assertThat(c.hashCode()).isEqualTo(eq.hashCode());

		final ConditionScript neq = sut.compileConditionScript(Collections.emptyList(), "1 == 1", 0);
		assertThat(c).isNotEqualTo(neq);
		assertThat(c.hashCode()).isNotEqualTo(neq.hashCode());
	}
}
