package net.thisptr.jmx.exporter.agent.config.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;

public class AttributeNamePatternDeserializer extends StdDeserializer<AttributeNamePattern> {
	private static final long serialVersionUID = 503000533123157062L;

	public AttributeNamePatternDeserializer() {
		super(AttributeNamePattern.class);
	}

	@Override
	public AttributeNamePattern deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final String patternText = p.readValueAs(String.class);
		if (patternText == null)
			return null;
		return AttributeNamePattern.compile(patternText);
	}
}
