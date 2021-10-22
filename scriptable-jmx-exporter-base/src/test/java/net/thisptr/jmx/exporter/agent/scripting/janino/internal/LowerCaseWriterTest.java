package net.thisptr.jmx.exporter.agent.scripting.janino.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.misc.StringWriter;

public class LowerCaseWriterTest {
	private static String toBytes(final String name, final StringWriter writer) {
		final byte[] bytes = new byte[writer.expectedSize(name)];
		final int size = writer.write(name, bytes, 0);
		return new String(bytes, 0, size, StandardCharsets.UTF_8);
	}

	private static String lowerCase(final String name) {
		return toBytes(name, LowerCaseWriter.getInstance());
	}

	@Test
	void testLowerCase() throws Exception {
		assertThat(lowerCase("java_lang:GarbageCollector:LastGcInfo.memoryUsageAfterGc.value.committed"))
				.isEqualTo("java_lang:garbagecollector:lastgcinfo_memoryusageaftergc_value_committed");
		assertThat(lowerCase("java_lang_GarbageCollector_LastGcInfo.memoryUsageAfterGc.value.committed"))
				.isEqualTo("java_lang_garbagecollector_lastgcinfo_memoryusageaftergc_value_committed");

		assertThat(lowerCase("0123456789*#")).isEqualTo("_0123456789__"); // _ prepended because metrics name cannot start with a number.

		assertThat(lowerCase("")).isEqualTo("_"); // _ because metrics cannot be empty.

		assertThat(lowerCase("test_:{test")).isEqualTo("test_:_test");
		assertThat(lowerCase("🎼あЛa\n\"\\")).isEqualTo("___a___");

		// leading capital is lowered without underscore.
		assertThat(lowerCase("Test")).isEqualTo("test");

		// duplicate colons are preserved.
		assertThat(lowerCase("java_lang::GarbageCollector"))
				.isEqualTo("java_lang::garbagecollector");

		assertThat(lowerCase("test:_test_underscore_after_colon")).isEqualTo("test:_test_underscore_after_colon");
		assertThat(lowerCase("test:__test_dup_underscore_after_colon")).isEqualTo("test:__test_dup_underscore_after_colon");
		assertThat(lowerCase("test__dup_underscore")).isEqualTo("test__dup_underscore");
		assertThat(lowerCase("_test_leading_underscore")).isEqualTo("_test_leading_underscore");
		assertThat(lowerCase("__test_leading_dup_underscore")).isEqualTo("__test_leading_dup_underscore");
		assertThat(lowerCase("test_:{Test_invalid_char_before_capital")).isEqualTo("test_:_test_invalid_char_before_capital");
		assertThat(lowerCase("test{Test_invalid_char_before_capital")).isEqualTo("test_test_invalid_char_before_capital");

		assertThat(lowerCase(":test_leading_colon")).isEqualTo(":test_leading_colon");
		assertThat(lowerCase(":Test_leading_colon_and_capital")).isEqualTo(":test_leading_colon_and_capital");
		assertThat(lowerCase("::test_leading_dup_colon")).isEqualTo("::test_leading_dup_colon");
		assertThat(lowerCase("test::test_dup_colon")).isEqualTo("test::test_dup_colon");
		assertThat(lowerCase("test::Test_dup_colon_and_capital")).isEqualTo("test::test_dup_colon_and_capital");
	}
}
