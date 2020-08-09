package net.thisptr.jmx.exporter.agent.jackson.serdes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.thisptr.jmx.exporter.agent.misc.ScriptText;

public class ScriptTextDeserializer extends StdDeserializer<ScriptText> {
	private static final long serialVersionUID = 745658273987032544L;

	public ScriptTextDeserializer() {
		super(ScriptText.class);
	}

	@Override
	public ScriptText deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final String text = p.readValueAs(String.class);
		if (text == null)
			return null;
		return ScriptText.valueOf(text);
	}
}
