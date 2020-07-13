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
		String text = p.readValueAs(String.class);
		if (text == null)
			return null;

		final SampleProcessorRegistry registry = SampleProcessorRegistry.getInstance();
		final SampleProcessor<?> scriptProcessor;
		final String scriptText;

		text = text.trim();
		if (text.startsWith("!")) {
			int i = 1;
			for (; i < text.length(); ++i) {
				final int ch = text.charAt(i);
				if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z')
					continue;
				break;
			}
			final String name = text.substring(1, i);
			scriptProcessor = registry.get(name);
			scriptText = text.substring(i);
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
