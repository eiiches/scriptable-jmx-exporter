package net.thisptr.jmx.exporter.agent.scripting.janino.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.scripting.janino.api.MetricValue;

public class V1Test {
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
