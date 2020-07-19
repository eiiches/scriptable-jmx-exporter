package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;

public class V1Test {

	@Test
	void testSnakeCase() throws Exception {
		final MetricValue m = new MetricValue();
		m.name = "java_lang:GarbageCollector:LastGcInfo_memoryUsageAfterGc_value_committed";
		V1.snakeCase().apply(m);
		assertThat(m.name).isEqualTo("java_lang_garbage_collector_last_gc_info_memory_usage_after_gc_value_committed");
	}
}
