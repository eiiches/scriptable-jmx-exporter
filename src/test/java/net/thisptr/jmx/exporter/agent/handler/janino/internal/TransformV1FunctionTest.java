package net.thisptr.jmx.exporter.agent.handler.janino.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.handler.janino.internal.TransformV1Function.Labels;

public class TransformV1FunctionTest {

	@Test
	void testLabels() throws Exception {
		final Labels labels = new Labels(1);
		labels.push("label", "1");
		labels.push("key", "1");
		labels.push("label", "2");
		labels.push("key", "2");
		labels.push("label", "3");
		labels.push("foo", "1");
		assertThat(labels.size()).isEqualTo(6);
		labels.pop("foo");
		assertThat(labels.size()).isEqualTo(5);
		labels.pop("label");
		assertThat(labels.size()).isEqualTo(4);
		labels.push("label", "3");
		assertThat(labels.size()).isEqualTo(5);

		final List<String> actual = new ArrayList<>();
		labels.forEach((label, value) -> {
			actual.add(label + "=" + value);
		});

		assertThat(actual).containsExactly("label=1", "key=1", "label_1=2", "key_1=2", "label_2=3");
	}

}
