package net.thisptr.jmx.exporter.agent.jackson.serdes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.thisptr.jmx.exporter.agent.handler.ScriptEngine;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngine.ScriptCompileException;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngineRegistry;
import net.thisptr.jmx.exporter.agent.handler.TransformScript;
import net.thisptr.jmx.exporter.agent.misc.ScriptText;

public class TransformScriptDeserializer extends StdDeserializer<TransformScript> {
	private static final long serialVersionUID = -2699557268566596799L;

	private static final String DEFAULT_ENGINE = "jq";

	public TransformScriptDeserializer() {
		super(TransformScript.class);
	}

	@Override
	public TransformScript deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String text = p.readValueAs(String.class);
		if (text == null)
			return null;

		final ScriptEngineRegistry registry = ScriptEngineRegistry.getInstance();
		final ScriptText scriptText = ScriptText.valueOf(text);
		final ScriptEngine scriptEngine = registry.get(scriptText.engineName != null ? scriptText.engineName : DEFAULT_ENGINE);

		try {
			return scriptEngine.compileTransformScript(scriptText.scriptBody);
		} catch (ScriptCompileException e) {
			throw new IOException(e);
		}
	}
}
