package net.thisptr.jmx.exporter.agent.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.thisptr.jmx.exporter.agent.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.handler.janino.JaninoScriptEngine;
import net.thisptr.jmx.exporter.agent.misc.FastObjectName;
import net.thisptr.jmx.exporter.agent.utils.MoreValidators;

public class ConfigTest {

	@Test
	void testDefaultIsValid() throws Exception {
		final Config sut = new Config();
		MoreValidators.validate(sut);
	}

	@Test
	void testNullsInRules() throws Exception {
		final Config sut = new Config();
		sut.rules.add(null);
		assertThrows(ValidationException.class, () -> MoreValidators.validate(sut));
	}

	@Test
	void testNullBindAddress() throws Exception {
		final Config sut = new Config();
		sut.server.bindAddress = null;
		assertThrows(ValidationException.class, () -> MoreValidators.validate(sut));
	}

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String TEST_JSON1 = "{"
			+ "\"declarations\": \"public static class Foo { public static boolean truefn() { return true; } } public static void test(MetricValueOutput out) { out.emit(new MetricValue()); }\","
			+ "\"rules\": [{"
			+ "  \"condition\": \"Foo.truefn()\","
			+ "  \"transform\": \"test(out);\""
			+ "}]}";

	private static final String TEST_JSON2 = "{"
			+ "\"rules\": [{"
			+ "  \"condition\": \"Foo.truefn()\","
			+ "  \"transform\": \"test(out);\""
			+ "}],"
			+ "\"declarations\": \"public static class Foo { public static boolean truefn() { return true; } } public static void test(MetricValueOutput out) { out.emit(new MetricValue()); }\""
			+ "}";

	@ParameterizedTest
	@ValueSource(strings = { TEST_JSON1, TEST_JSON2 })
	void testDeserialize(final String json) throws Exception {
		final ObjectName name = new ObjectName("java.lang:type=Memory");
		final MBeanInfo mbeanInfo = ManagementFactory.getPlatformMBeanServer().getMBeanInfo(name);
		final MBeanAttributeInfo attributeInfo = mbeanInfo.getAttributes()[0];

		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("java", new JaninoScriptEngine());
		final Config config = MAPPER.readValue(json, Config.class);

		assertThat(config.rules.get(0).condition.evaluate(null, null)).isTrue();
		final List<PrometheusMetric> ms = new ArrayList<>();
		config.rules.get(0).transform.execute(new Sample<PrometheusScrapeRule>(null, Collections.emptyMap(), 0L, new FastObjectName(name), mbeanInfo, attributeInfo, null), ms::add);
		assertThat(ms).hasSize(1);
		assertThat(ms.get(0)).isNotNull();
		assertThat(config.server).isNotNull();
		assertThat(config.options).isNotNull();
	}
}
