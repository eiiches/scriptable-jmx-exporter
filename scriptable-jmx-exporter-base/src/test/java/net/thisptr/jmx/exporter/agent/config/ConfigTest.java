package net.thisptr.jmx.exporter.agent.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.validation.ValidationException;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.thisptr.jmx.exporter.agent.Agent;
import net.thisptr.jmx.exporter.agent.config.Config.ScrapeRule;
import net.thisptr.jmx.exporter.agent.misc.FastObjectName;
import net.thisptr.jmx.exporter.agent.scraper.Sample;
import net.thisptr.jmx.exporter.agent.scripting.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.scripting.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.scripting.janino.JaninoScriptEngine;
import net.thisptr.jmx.exporter.agent.utils.MoreValidators;

public class ConfigTest {
	static {
		try {
			Class.forName(Agent.class.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void testDefaultIsValid() throws Exception {
		final Config sut = Config.createDefault();
		MoreValidators.validate(sut);
	}

	@Test
	void testNullsInRules() throws Exception {
		final Config sut = Config.createDefault();
		sut.rules.add(null);
		assertThrows(ValidationException.class, () -> MoreValidators.validate(sut));
	}

	@Test
	void testNullBindAddress() throws Exception {
		final Config sut = Config.createDefault();
		sut.server.bindAddress = null;
		assertThrows(ValidationException.class, () -> MoreValidators.validate(sut));
	}

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

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
		final Config config = Config.merge(Arrays.asList(MAPPER.readValue(json, Config.class)));

		assertThat(config.rules.get(0).condition.evaluate(null, null)).isTrue();
		final List<PrometheusMetric> ms = new ArrayList<>();
		config.rules.get(0).transform.execute(new Sample(null, Collections.emptyMap(), 0L, new FastObjectName(name), mbeanInfo, attributeInfo, null), ms::add);
		assertThat(ms).hasSize(1);
		assertThat(ms.get(0)).isNotNull();
		assertThat(config.server).isNotNull();
		assertThat(config.options).isNotNull();
	}

	@Test
	void testSkipTrueAndNonNullTransform() throws Exception {
		// https://github.com/eiiches/scriptable-jmx-exporter/issues/10#issuecomment-672887503
		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("java", new JaninoScriptEngine());
		final Config config = Config.merge(Arrays.asList(YAML_MAPPER.readValue(""
				+ "rules:\n"
				+ "- pattern: 'foo'\n"
				+ "  skip: true\n"
				+ "  transform: ''\n"
				+ "", Config.class)));
		assertThatThrownBy(() -> MoreValidators.validate(config))
				.isInstanceOf(ValidationException.class)
				.hasMessageContaining("transform script must not be specified");
	}

	@TestFactory
	Stream<DynamicTest> testSkipFalseAndNullTransform() throws Exception {
		// https://github.com/eiiches/scriptable-jmx-exporter/issues/10#issuecomment-672887503
		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("java", new JaninoScriptEngine());
		return Stream.of("  skip: null\n", "  skip: false\n", "").flatMap((skip) -> {
			return Stream.of("  transform: null\n", "").flatMap((transform) -> {
				final String yaml = ""
						+ "rules:\n"
						+ "- pattern: 'foo'\n"
						+ skip
						+ transform
						+ "";
				return Stream.of(dynamicTest(yaml, () -> {
					final Config config = Config.merge(Arrays.asList(YAML_MAPPER.readValue(yaml, Config.class)));
					assertThatThrownBy(() -> MoreValidators.validate(config))
							.isInstanceOf(ValidationException.class)
							.hasMessageContaining("transform script must be provided");
				}));
			});
		});
	}

	@Test
	void testAllRulesAreReachableValidation() throws Exception {
		final ScrapeRule rule0 = new ScrapeRule();
		rule0.patterns = null;
		rule0.condition = null;
		rule0.skip = true;

		final ScrapeRule rule1 = new ScrapeRule();
		rule1.patterns = null;
		rule1.condition = null;
		rule1.skip = true;

		final ScrapeRule rule2 = new ScrapeRule();
		rule2.patterns = null;
		rule2.condition = null;
		rule2.skip = true;

		final Config sut = Config.createDefault();
		sut.rules = Arrays.asList(rule0, rule1, rule2);

		assertThatThrownBy(() -> MoreValidators.validate(sut))
				.isInstanceOf(ValidationException.class)
				.hasMessageContaining("rule is not reachable");
	}
}
