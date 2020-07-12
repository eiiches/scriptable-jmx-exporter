package net.thisptr.java.prometheus.metrics.agent.jackson.serdes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.thisptr.java.prometheus.metrics.agent.handler.SampleProcessor;
import net.thisptr.java.prometheus.metrics.agent.handler.SampleProcessor.ScriptCompileException;
import net.thisptr.java.prometheus.metrics.agent.handler.SampleProcessorRegistry;
import net.thisptr.java.prometheus.metrics.agent.handler.Script;

public class ScriptDeserializer extends StdDeserializer<Script<?>> {
	private static final long serialVersionUID = -2699557268566596799L;

	public ScriptDeserializer() {
		super(Script.class);
	}

	@Override
	public Script<?> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final String text = p.readValueAs(String.class);
		if (text == null)
			return null;

		final SampleProcessorRegistry registry = SampleProcessorRegistry.getInstance();
		final SampleProcessor<?> scriptProcessor;
		final String scriptText;
		if (text.startsWith("!")) {
			final String[] tokens = text.substring(1).split(" ", 2);
			final String name = tokens[0];
			scriptProcessor = registry.get(name);
			scriptText = tokens[1];
		} else {
			scriptProcessor = registry.get();
			scriptText = text;
		}
		try {
			return scriptProcessor.compile(scriptText);
		} catch (ScriptCompileException e) {
			throw new IOException(e);
		}
	}
}
