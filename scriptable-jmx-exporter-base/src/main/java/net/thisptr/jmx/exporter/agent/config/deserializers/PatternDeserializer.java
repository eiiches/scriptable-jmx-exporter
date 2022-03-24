package net.thisptr.jmx.exporter.agent.config.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.regex.Pattern;

public class PatternDeserializer extends StdDeserializer<Pattern> {
    public PatternDeserializer() {
        super(Pattern.class);
    }

    @Override
    public Pattern deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final String patternText = p.readValueAs(String.class);
        if (patternText == null)
            return null;
        return Pattern.compile(patternText);
    }
}
