package net.thisptr.jmx.exporter.agent.handler.janino.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.v1.V1.LowerCaseWriter;
import net.thisptr.jmx.exporter.agent.handler.janino.api.v1.V1.SnakeCaseWriter;
import net.thisptr.jmx.exporter.agent.misc.StringWriter;

public class V1Test {

	private static String toBytes(final String name, final StringWriter writer) {
		final byte[] bytes = new byte[writer.expectedSize(name)];
		final int size = writer.write(name, bytes, 0);
		return new String(bytes, 0, size, StandardCharsets.UTF_8);
	}

	private static String snakeCase(final String name) {
		return toBytes(name, SnakeCaseWriter.getInstance());
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

	@Test
	void testSnakeCase() throws Exception {
		assertThat(snakeCase("java_lang:GarbageCollector:LastGcInfo.memoryUsageAfterGc.value.committed"))
				.isEqualTo("java_lang:garbage_collector:last_gc_info_memory_usage_after_gc_value_committed");
		assertThat(snakeCase("java_lang_GarbageCollector_LastGcInfo.memoryUsageAfterGc.value.committed"))
				.isEqualTo("java_lang_garbage_collector_last_gc_info_memory_usage_after_gc_value_committed");

		assertThat(snakeCase("0123456789*#")).isEqualTo("_0123456789__"); // _ prepended because metrics name cannot start with a number.

		assertThat(snakeCase("")).isEqualTo("_"); // _ because metrics cannot be empty.

		assertThat(snakeCase("test_:{test")).isEqualTo("test_:_test");
		assertThat(snakeCase("🎼あЛa\n\"\\")).isEqualTo("___a___");

		// leading capital is lowered without underscore.
		assertThat(snakeCase("Test")).isEqualTo("test");

		// duplicate colons are preserved.
		assertThat(snakeCase("java_lang::GarbageCollector"))
				.isEqualTo("java_lang::garbage_collector");

		assertThat(snakeCase("test:_test_underscore_after_colon")).isEqualTo("test:_test_underscore_after_colon");
		assertThat(snakeCase("test:__test_dup_underscore_after_colon")).isEqualTo("test:__test_dup_underscore_after_colon");
		assertThat(snakeCase("test__dup_underscore")).isEqualTo("test__dup_underscore");
		assertThat(snakeCase("_test_leading_underscore")).isEqualTo("_test_leading_underscore");
		assertThat(snakeCase("__test_leading_dup_underscore")).isEqualTo("__test_leading_dup_underscore");
		assertThat(snakeCase("test_:{Test_invalid_char_before_capital")).isEqualTo("test_:_test_invalid_char_before_capital");
		assertThat(snakeCase("test{Test_invalid_char_before_capital")).isEqualTo("test_test_invalid_char_before_capital");

		assertThat(snakeCase(":test_leading_colon")).isEqualTo(":test_leading_colon");
		assertThat(snakeCase(":Test_leading_colon_and_capital")).isEqualTo(":test_leading_colon_and_capital");
		assertThat(snakeCase("::test_leading_dup_colon")).isEqualTo("::test_leading_dup_colon");
		assertThat(snakeCase("test::test_dup_colon")).isEqualTo("test::test_dup_colon");
		assertThat(snakeCase("test::Test_dup_colon_and_capital")).isEqualTo("test::test_dup_colon_and_capital");

		assertThat(snakeCase("Catalina_SSLHostConfig_certificateVerificationDepth")).isEqualTo("catalina_ssl_host_config_certificate_verification_depth");
		assertThat(snakeCase("Catalina:SSLHostConfig:certificateVerificationDepth")).isEqualTo("catalina:ssl_host_config:certificate_verification_depth");

		// This case-style is just weird. I don't think we can support this without compromising something.
		assertThat(snakeCase("Catalina_ProtocolHandler_sSLVerifyDepth")).isEqualTo("catalina_protocol_handler_s_sl_verify_depth");
		assertThat(snakeCase("Catalina:ProtocolHandler:sSLVerifyDepth")).isEqualTo("catalina:protocol_handler:s_sl_verify_depth");

		assertThat(snakeCase("Catalina_SSLH{stConfig_certificateVerificationDepth")).isEqualTo("catalina_sslh_st_config_certificate_verification_depth");
		assertThat(snakeCase("Catalina_SSLH_stConfig_certificateVerificationDepth")).isEqualTo("catalina_sslh_st_config_certificate_verification_depth");

		assertThat(snakeCase("Catalina_SSL")).isEqualTo("catalina_ssl");
		assertThat(snakeCase("Catalina_S")).isEqualTo("catalina_s");

		assertThat(snakeCase("Catalina:ProtocolHandler:Foo_SSLVerifyDepth")).isEqualTo("catalina:protocol_handler:foo_ssl_verify_depth");
		assertThat(snakeCase("Catalina:ProtocolHandler:FooSSLVerifyDepth")).isEqualTo("catalina:protocol_handler:foo_ssl_verify_depth");
		assertThat(snakeCase("Catalina:ProtocolHandler:Foo:SSLVerifyDepth")).isEqualTo("catalina:protocol_handler:foo:ssl_verify_depth");
		assertThat(snakeCase("Catalina:ProtocolHandler:Foo{SSLVerifyDepth")).isEqualTo("catalina:protocol_handler:foo_ssl_verify_depth");

		assertThat(snakeCase("SSL")).isEqualTo("ssl");
		assertThat(snakeCase("SSL{test")).isEqualTo("ssl_test");
		assertThat(snakeCase("SSL_test")).isEqualTo("ssl_test");
		assertThat(snakeCase("SSLtest")).isEqualTo("ss_ltest");
		assertThat(snakeCase("SSLTest")).isEqualTo("ssl_test");

		assertThat(snakeCase("STest")).isEqualTo("s_test");
		assertThat(snakeCase("TestS")).isEqualTo("test_s");
		assertThat(snakeCase("{STest")).isEqualTo("_s_test");
		assertThat(snakeCase("0STest")).isEqualTo("_0_s_test");
		assertThat(snakeCase("0sTest")).isEqualTo("_0s_test");
		assertThat(snakeCase("aSTest")).isEqualTo("a_s_test");
		assertThat(snakeCase("aaSTest")).isEqualTo("aa_s_test");
	}

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
