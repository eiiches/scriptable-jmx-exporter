package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.v1.V1.SnakeCaseWriter;

public class V1Test {

	private static String snakeCase(final String name) {
		final SnakeCaseWriter writer = SnakeCaseWriter.getInstance();
		final byte[] bytes = new byte[writer.expectedSize(name)];
		final int size = writer.write(name, bytes, 0);
		return new String(bytes, 0, size, StandardCharsets.UTF_8);
	}

	@Test
	void testSnakeCase() throws Exception {
		assertThat(snakeCase("java_lang:GarbageCollector:LastGcInfo_memoryUsageAfterGc_value_committed"))
				.isEqualTo("java_lang_garbage_collector_last_gc_info_memory_usage_after_gc_value_committed");
		assertThat(snakeCase("0123456789*#")).isEqualTo("_0123456789_"); // _ prepended because metrics name cannot start with a number.
		assertThat(snakeCase("")).isEqualTo("_"); // _ because metrics cannot be empty.
		assertThat(snakeCase("test_:{test")).isEqualTo("test_test");
		assertThat(snakeCase("🎼あЛa\n\"\\")).isEqualTo("_a_");
		assertThat(snakeCase("Test")).isEqualTo("test");
	}

	@Test
	void testBuilderStyle() throws Exception {
		final Map<String, String> keyProperties = new HashMap<>();
		keyProperties.put("type", "GarbageCollector");
		keyProperties.put("name", "G1 Young Generation");

		final long timestamp = System.currentTimeMillis();

		final List<MetricValue> out = new ArrayList<>();

		V1.name(':', "java.lang", keyProperties.get("type"), "CollectionCount")
				.addLabelsExcluding(keyProperties, "type")
				.type("counter")
				.help("The total number of garbage collections")
				.timestamp(timestamp)
				.transform(1.0, "double", out::add);

		assertThat(out).hasSize(1);
		assertThat(out.get(0).name).isEqualTo("java.lang:GarbageCollector:CollectionCount");
		assertThat(out.get(0).value).isEqualTo(1.0);
		assertThat(out.get(0).type).isEqualTo("counter");
		assertThat(out.get(0).help).isEqualTo("The total number of garbage collections");
		assertThat(out.get(0).timestamp).isEqualTo(timestamp);
		assertThat(out.get(0).labels).hasSize(1);
		assertThat(out.get(0).labels).containsEntry("name", "G1 Young Generation");
	}
}
