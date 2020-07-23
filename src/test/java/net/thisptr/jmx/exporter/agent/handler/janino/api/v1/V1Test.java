package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

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
}
