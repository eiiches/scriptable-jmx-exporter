package net.thisptr.jmx.exporter.agent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class PrometheusMetricWriterTest {
	@Test
	void testWriteWithEmptyMetricAndLabelName() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrometheusMetricWriter writer = new PrometheusMetricWriter(new OutputStreamWritableByteChannel(baos), () -> {}, ByteBuffer.allocate(16 * 1024), false)) {
			final PrometheusMetric metric = new PrometheusMetric();
			metric.name = "";
			metric.value = 1.0;
			metric.labels = new HashMap<>();
			metric.labels.put("", "foo");
			writer.write(metric);
		}
		assertThat(baos.toByteArray()).isEqualTo("_{_=\"foo\",} 1\n".getBytes(StandardCharsets.UTF_8));
	}

	@Test
	void testWriteWithLabels() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrometheusMetricWriter writer = new PrometheusMetricWriter(new OutputStreamWritableByteChannel(baos), () -> {}, ByteBuffer.allocate(16 * 1024), true)) {
			final PrometheusMetric metric = new PrometheusMetric();
			metric.name = "metricName_a:@";
			metric.value = 1.0;
			metric.labels = new HashMap<>();
			metric.labels.put("labelName_b:@", "foo");
			metric.timestamp = 10000000000000L;
			writer.write(metric);
		}
		assertThat(baos.toByteArray()).isEqualTo("metricName_a:_{labelName_b__=\"foo\",} 1 10000000000000\n".getBytes(StandardCharsets.UTF_8));
	}

	@Test
	void testWriteWithEmptyLabels() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrometheusMetricWriter writer = new PrometheusMetricWriter(new OutputStreamWritableByteChannel(baos), () -> {}, ByteBuffer.allocate(16 * 1024), false)) {
			final PrometheusMetric metric = new PrometheusMetric();
			metric.name = "metricName_a:@";
			metric.value = 1.0;
			metric.labels = new HashMap<>();
			writer.write(metric);
		}
		assertThat(baos.toByteArray()).isEqualTo("metricName_a:_ 1\n".getBytes(StandardCharsets.UTF_8));
	}
}
