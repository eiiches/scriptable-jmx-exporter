package net.thisptr.jmx.exporter.agent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import net.thisptr.jmx.exporter.agent.misc.StringWriter;

public class PrometheusMetricWriterTest {
	private static String toString(final PrometheusMetric m, final int bufSize, final boolean includeTimestamp) throws IOException {
		return toString(bufSize, includeTimestamp, (writer) -> {
			writer.write(m);
		});
	}

	private static final class LowerCaseStringWriter implements StringWriter {
		@Override
		public int write(final String name, final byte[] bytes, final int index) {
			final byte[] src = name.toLowerCase().getBytes(StandardCharsets.UTF_8);
			System.arraycopy(src, 0, bytes, index, src.length);
			return index + src.length;
		}

		@Override
		public int expectedSize(final String name) {
			return name.length();
		}
	}

	private interface PrometheusMetricWriterTask {
		void execute(PrometheusMetricWriter writer) throws IOException;
	}

	private static String toString(final int bufSize, final boolean includeTimestamp, final PrometheusMetricWriterTask fn) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrometheusMetricWriter writer = new PrometheusMetricWriter(new OutputStreamWritableByteChannel(baos), () -> {}, ByteBuffer.allocate(bufSize), includeTimestamp)) {
			fn.execute(writer);
		}
		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}

	@RepeatedTest(50)
	void testUnicodeInLabels(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "test";
		m.value = 1.0;
		m.labels = new HashMap<>();
		m.labels.put("🎼あЛa\n\"\\", "🎼あЛa\n\" \\ ");
		assertThat(toString(m, info.getCurrentRepetition(), false)).isEqualTo("test{___a___=\"🎼あЛa\\n\\\" \\\\ \",} 1\n");
	}

	@RepeatedTest(20)
	void testUnicodeInMetricNames(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "🎼あЛa\n\"\\";
		m.value = 1.0;
		m.labels = new HashMap<>();
		assertThat(toString(m, info.getCurrentRepetition(), false)).isEqualTo("___a___ 1\n");
	}

	@RepeatedTest(20)
	void testNumberInLabelNames(final RepetitionInfo info) throws IOException {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "test";
		m.value = 1.0;
		m.labels = new HashMap<>();
		m.labels.put("0123456789a#", "");
		assertThat(toString(m, info.getCurrentRepetition(), false)).isEqualTo("test{_123456789a_=\"\",} 1\n");
	}

	@RepeatedTest(20)
	void testNumberInMeticName(final RepetitionInfo info) throws IOException {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "00123456789a#";
		m.value = 1.0;
		m.labels = new HashMap<>();
		assertThat(toString(m, info.getCurrentRepetition(), false)).isEqualTo("_0123456789a_ 1\n");
	}

	@RepeatedTest(20)
	void testWriteWithSmallBuffer(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "";
		m.value = 1.0;
		m.labels = new HashMap<>();
		m.labels.put("", "foo");
		assertThat(toString(m, info.getCurrentRepetition(), false)).isEqualTo("_{_=\"foo\",} 1\n");
	}

	@RepeatedTest(20)
	void testWriteWithEmptyMetricAndLabelName(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "";
		m.value = 1.0;
		m.labels = new HashMap<>();
		m.labels.put("", "foo");
		assertThat(toString(m, info.getCurrentRepetition(), false)).isEqualTo("_{_=\"foo\",} 1\n");
	}

	@RepeatedTest(50)
	void testWriteWithLabels(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "metricName_a:@";
		m.value = 1.0;
		m.labels = new HashMap<>();
		m.labels.put("labelName_b:@", "foo");
		m.timestamp = 10000000000000L;
		assertThat(toString(m, info.getCurrentRepetition(), true)).isEqualTo("metricName_a:_{labelName_b__=\"foo\",} 1 10000000000000\n");
	}

	@RepeatedTest(20)
	void testFloatingPointValues(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "test";
		m.value = 1.5;
		assertThat(toString(m, info.getCurrentRepetition(), true)).isEqualTo("test 1.5\n");
	}

	@RepeatedTest(20)
	void testNullLabels(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "test";
		m.value = 1.0;
		m.labels = new HashMap<>();
		m.labels.put("foo", null);
		assertThat(toString(m, info.getCurrentRepetition(), true)).isEqualTo("test{foo=\"null\",} 1\n");
	}

	@RepeatedTest(20)
	void testEmptySuffix(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "test";
		m.value = 1.0;
		m.suffix = "";
		assertThat(toString(m, info.getCurrentRepetition(), true)).isEqualTo("test 1\n");
	}

	@RepeatedTest(20)
	void testSuffix(final RepetitionInfo info) throws Exception {
		final PrometheusMetric m = new PrometheusMetric();
		m.name = "test";
		m.value = 1.0;
		m.suffix = "sum";
		assertThat(toString(m, info.getCurrentRepetition(), true)).isEqualTo("test_sum 1\n");
	}

	@RepeatedTest(20)
	void testWriteWithEmptyLabels(final RepetitionInfo info) throws Exception {
		final PrometheusMetric metric = new PrometheusMetric();
		metric.name = "metricName_a:@";
		metric.value = 1.0;
		metric.labels = new HashMap<>();
		assertThat(toString(metric, info.getCurrentRepetition(), false)).isEqualTo("metricName_a:_ 1\n");
	}

	@RepeatedTest(20)
	void testType(final RepetitionInfo info) throws Exception {
		final String actual = toString(info.getCurrentRepetition(), true, (w) -> {
			w.writeType("test", null, null, "counter");
		});
		assertThat(actual).isEqualTo("# TYPE test counter\n");
	}

	@RepeatedTest(30)
	void testHelp(final RepetitionInfo info) throws Exception {
		final String actual = toString(info.getCurrentRepetition(), true, (w) -> {
			w.writeHelp("test", null, null, "🎼あЛa\n\" \\ ");
		});
		assertThat(actual).isEqualTo("# HELP test 🎼あЛa\\n\" \\\\ \n");
	}

	@RepeatedTest(20)
	void testCustomNameWriter(final RepetitionInfo info) throws Exception {
		final PrometheusMetric metric = new PrometheusMetric();
		metric.name = "TEST";
		metric.nameWriter = new LowerCaseStringWriter();
		metric.value = 1.0;
		metric.labels = new HashMap<>();
		assertThat(toString(metric, info.getCurrentRepetition(), false)).isEqualTo("test 1\n");
	}

	@RepeatedTest(20)
	void testHelpWithCustomNameWriter(final RepetitionInfo info) throws Exception {
		final String actual = toString(info.getCurrentRepetition(), true, (w) -> {
			w.writeHelp("TEST", new LowerCaseStringWriter(), null, "value");
		});
		assertThat(actual).isEqualTo("# HELP test value\n");
	}

	@RepeatedTest(20)
	void testTypeWithCustomNameWriter(final RepetitionInfo info) throws Exception {
		final String actual = toString(info.getCurrentRepetition(), true, (w) -> {
			w.writeType("TEST", new LowerCaseStringWriter(), null, "counter");
		});
		assertThat(actual).isEqualTo("# TYPE test counter\n");
	}
}
