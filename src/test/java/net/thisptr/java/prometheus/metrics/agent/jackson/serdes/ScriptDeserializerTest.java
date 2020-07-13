package net.thisptr.java.prometheus.metrics.agent.jackson.serdes;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.thisptr.java.prometheus.metrics.agent.handler.ScriptEngineRegistry;
import net.thisptr.java.prometheus.metrics.agent.handler.Script;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.JaninoScriptEngine;
import net.thisptr.java.prometheus.metrics.agent.handler.jq.JsonQueryScriptEngine;

public class ScriptDeserializerTest {

	static {
		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("jq", new JsonQueryScriptEngine());
		registry.add("java", new JaninoScriptEngine());
		registry.setDefault("jq");
	}

	private final ObjectMapper MAPPER = new ObjectMapper()
			.registerModule(new SimpleModule()
					.addDeserializer(Script.class, new ScriptDeserializer()));

	@Test
	void testDeserialize() throws Exception {
		assertThatCode(() -> MAPPER.readValue("\"!java System.out.println()\"", Script.class)).doesNotThrowAnyException();

		// random whitespaces
		assertThatCode(() -> MAPPER.readValue("\" \\n!java System.out.println()\"", Script.class)).doesNotThrowAnyException();
		assertThatCode(() -> MAPPER.readValue("\" \\n!java\\n\\nSystem.out.println()\"", Script.class)).doesNotThrowAnyException();

		// default jq
		assertThatCode(() -> MAPPER.readValue("\"1\"", Script.class)).doesNotThrowAnyException();
	}

	@Test
	void testInvalidDeserialize() throws Exception {
		assertThatThrownBy(() -> MAPPER.readValue("\"!java 1\"", Script.class)).isInstanceOf(Exception.class); // expression is not allowed
		assertThatThrownBy(() -> MAPPER.readValue("\"System.out.println()\"", Script.class)).isInstanceOf(Exception.class); // java not allowed in jq mode
	}
}
