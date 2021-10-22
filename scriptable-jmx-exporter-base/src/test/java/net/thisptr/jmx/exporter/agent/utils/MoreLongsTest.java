package net.thisptr.jmx.exporter.agent.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MoreLongsTest {
	private final Random r = new Random();

	@RepeatedTest(value = 10000)
	void testStringSizeRandom() throws Exception {
		long x = r.nextLong();
		if (x < 0)
			x = -x;
		assertThat(MoreLongs.stringSize(x)).isEqualTo(Long.toString(x).length());
	}

	@Test
	void testStringSize() throws Exception {
		long x = 1;
		for (int i = 0; i < 19; ++i) {
			assertThat(MoreLongs.stringSize(x)).isEqualTo(Long.toString(x).length());
			assertThat(MoreLongs.stringSize(x + 1)).isEqualTo(Long.toString(x + 1).length());
			assertThat(MoreLongs.stringSize(x - 1)).isEqualTo(Long.toString(x - 1).length());
			x *= 10;
		}
	}

	@Test
	void testStringSizeMax() throws Exception {
		assertThat(MoreLongs.stringSize(0)).isEqualTo(Long.toString(0).length());
		assertThat(MoreLongs.stringSize(Long.MAX_VALUE)).isEqualTo(Long.toString(Long.MAX_VALUE).length());
	}

	@RepeatedTest(value = 10000)
	void testWriteAsStringRandom() {
		final byte[] bytes = new byte[64];
		final long x = r.nextLong();
		final int index = r.nextInt(10);
		final int newIndex = MoreLongs.writeAsString(x, bytes, index);
		final String expected = Long.toString(x);
		assertThat(newIndex - index).isEqualTo(expected.length());
		assertThat(new String(bytes, index, newIndex - index, StandardCharsets.UTF_8)).isEqualTo(expected);
	}

	@ParameterizedTest
	@ValueSource(longs = { Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE })
	void testWriteAsStringMax(final long x) {
		final byte[] bytes = new byte[64];
		final int index = r.nextInt(10);
		final int newIndex = MoreLongs.writeAsString(x, bytes, index);
		final String expected = Long.toString(x);
		assertThat(newIndex - index).isEqualTo(expected.length());
		assertThat(new String(bytes, index, newIndex - index, StandardCharsets.UTF_8)).isEqualTo(expected);
	}
}
