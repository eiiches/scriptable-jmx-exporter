package net.thisptr.java.prometheus.metrics.agent.jackson.serdes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.thisptr.jackson.jq.JsonQuery;

public class JsonQueryDeserializer extends StdDeserializer<JsonQuery> {
	private static final long serialVersionUID = -5919966550962626465L;

	public JsonQueryDeserializer() {
		super(JsonQuery.class);
	}

	@Override
	public JsonQuery deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final String jqText = p.readValueAs(String.class);
		if (jqText == null)
			return null;
		return JsonQuery.compile(jqText);
	}
}
