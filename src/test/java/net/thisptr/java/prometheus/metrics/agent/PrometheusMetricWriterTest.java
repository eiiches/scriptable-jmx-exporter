package net.thisptr.java.prometheus.metrics.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class PrometheusMetricWriterTest {
	@Test
	void testWriteWithEmptyMetricAndLabelName() throws Exception {
		final StringBuilder builder = new StringBuilder();
		try (PrometheusMetricWriter writer = new PrometheusMetricWriter(builder, false)) {
			final PrometheusMetric metric = new PrometheusMetric();
			metric.name = "";
			metric.value = 1.0;
			metric.labels = new HashMap<>();
			metric.labels.put("", "foo");
			writer.write(metric);
		}
		assertEquals("_{_=\"foo\",} 1.0\n", builder.toString());
	}

	@Test
	void testWriteWithLabels() throws Exception {
		final StringBuilder builder = new StringBuilder();
		try (PrometheusMetricWriter writer = new PrometheusMetricWriter(builder, true)) {
			final PrometheusMetric metric = new PrometheusMetric();
			metric.name = "metricName_a:@";
			metric.value = 1.0;
			metric.labels = new HashMap<>();
			metric.labels.put("labelName_b:@", "foo");
			metric.timestamp = 10000000000000L;
			writer.write(metric);
		}
		assertEquals("metricName_a:_{labelName_b__=\"foo\",} 1.0 10000000000000\n", builder.toString());
	}

	@Test
	void testWriteWithEmptyLabels() throws Exception {
		final StringBuilder builder = new StringBuilder();
		try (PrometheusMetricWriter writer = new PrometheusMetricWriter(builder, false)) {
			final PrometheusMetric metric = new PrometheusMetric();
			metric.name = "metricName_a:@";
			metric.value = 1.0;
			metric.labels = new HashMap<>();
			writer.write(metric);
		}
		assertEquals("metricName_a:_ 1.0\n", builder.toString());
	}
}
