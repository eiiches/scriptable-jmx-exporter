package net.thisptr.jmx.exporter.agent.jackson.serdes;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.thisptr.jmx.exporter.agent.handler.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.handler.TransformScript;
import net.thisptr.jmx.exporter.agent.handler.janino.JaninoScriptEngine;
import net.thisptr.jmx.exporter.agent.handler.jq.JsonQueryScriptEngine;

public class TransformScriptDeserializerTest {

	static {
		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		registry.add("jq", new JsonQueryScriptEngine());
		registry.add("java", new JaninoScriptEngine());
	}

	private final ObjectMapper MAPPER = new ObjectMapper()
			.registerModule(new SimpleModule()
					.addDeserializer(TransformScript.class, new TransformScriptDeserializer()));

	@Test
	void testDeserialize() throws Exception {
		assertThatCode(() -> MAPPER.readValue("\"!java System.out.println()\"", TransformScript.class)).doesNotThrowAnyException();

		// random whitespaces
		assertThatCode(() -> MAPPER.readValue("\" \\n!java System.out.println()\"", TransformScript.class)).doesNotThrowAnyException();
		assertThatCode(() -> MAPPER.readValue("\" \\n!java\\n\\nSystem.out.println()\"", TransformScript.class)).doesNotThrowAnyException();

		// default jq
		assertThatCode(() -> MAPPER.readValue("\"1\"", TransformScript.class)).doesNotThrowAnyException();
	}

	@Test
	void testInvalidDeserialize() throws Exception {
		assertThatThrownBy(() -> MAPPER.readValue("\"!java 1\"", TransformScript.class)).isInstanceOf(Exception.class); // expression is not allowed
		assertThatThrownBy(() -> MAPPER.readValue("\"System.out.println()\"", TransformScript.class)).isInstanceOf(Exception.class); // java not allowed in jq mode
	}
}
