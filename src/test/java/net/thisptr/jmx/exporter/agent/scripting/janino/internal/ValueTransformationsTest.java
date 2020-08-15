package net.thisptr.jmx.exporter.agent.scripting.janino.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
}
