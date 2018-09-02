package net.thisptr.java.prometheus.metrics.agent.jackson.serdes;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thisptr.jackson.jq.JsonQuery;

public class LabelsDeserializer extends JsonQueryDeserializer {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final long serialVersionUID = -5919966550962626465L;

	@Override
	public JsonQuery deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		if (p.currentToken() == JsonToken.START_OBJECT) {
			final Map<String, String> labels = p.readValueAs(new TypeReference<Map<String, String>>() {});
			return JsonQuery.compile(MAPPER.valueToTree(labels).toString());
		}
		return super.deserialize(p, ctxt);
	}
}
