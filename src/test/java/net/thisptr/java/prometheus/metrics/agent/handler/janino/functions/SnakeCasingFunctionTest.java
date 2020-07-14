package net.thisptr.java.prometheus.metrics.agent.handler.janino.functions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.MetricValue;

public class SnakeCasingFunctionTest {

	@Test
	void testSnakeCase() throws Exception {
		final List<MetricValue> actual = new ArrayList<>();

		final MetricValue m = new MetricValue();
		m.name = "java_lang:GarbageCollector:LastGcInfo_memoryUsageAfterGc_value_committed";
		SnakeCasingFunction.snakeCasing(actual::add).emit(m);

		assertThat(actual.size()).isEqualTo(1);
		assertThat(actual.get(0).name).isEqualTo("java_lang_garbage_collector_last_gc_info_memory_usage_after_gc_value_committed");
	}
}
