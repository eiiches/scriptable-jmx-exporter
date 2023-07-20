package net.thisptr.jmx.exporter.agent.scripting.janino.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.thisptr.jmx.exporter.agent.scripting.janino.api.MetricValue;

public class ValueTransformationsTest {

	@ParameterizedTest
	@ValueSource(strings = { "java.lang.Object", "[J" })
	public void testUnfoldIntArray(final String type) {
		final List<MetricValue> metrics = new ArrayList<>();
		ValueTransformations.unfold(new MetricNamer(0), new Labels(0), new int[] { 3, 4 }, type, metrics::add);
		assertThat(metrics).hasSize(2);
		assertThat(metrics.get(0).value).isEqualTo(3);
		assertThat(metrics.get(0).labels.get("index")).isEqualTo("0");
		assertThat(metrics.get(1).value).isEqualTo(4);
		assertThat(metrics.get(1).labels.get("index")).isEqualTo("1");
	}

	@ParameterizedTest
	@ValueSource(strings = { "java.lang.Object", "java.util.Date" })
	void testDate(final String type) {
		final Date date = new Date();
		final List<MetricValue> metrics = new ArrayList<>();
		ValueTransformations.unfold(new MetricNamer(0), new Labels(0), date, type, metrics::add);
		assertThat(metrics.get(0).value).isEqualTo(date.getTime() / 1000.0);
	}

	@ParameterizedTest
	@ValueSource(strings = { "java.lang.Object", "java.util.Set" })
	void testSet(final String type) {
		final Set<String> set = new LinkedHashSet<>(Arrays.asList("a", "b"));
		final List<MetricValue> metrics = new ArrayList<>();
		ValueTransformations.unfold(new MetricNamer(0), new Labels(0), set, type, metrics::add);
		assertThat(metrics).hasSize(2);
		assertThat(metrics.get(0).value).isEqualTo(1.0);
		assertThat(metrics.get(0).labels.get("key")).isEqualTo("a");
		assertThat(metrics.get(1).value).isEqualTo(1.0);
		assertThat(metrics.get(1).labels.get("key")).isEqualTo("b");
	}

	@Test
	void testNumber() {
		final List<MetricValue> metrics = new ArrayList<>();
		ValueTransformations.unfold(new MetricNamer(0), new Labels(0), Long.valueOf(10), "java.lang.Number", metrics::add);
		assertThat(metrics).hasSize(1);
		assertThat(metrics.get(0).value).isEqualTo(10.0);
	}
}
