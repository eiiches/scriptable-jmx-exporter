package net.thisptr.jmx.exporter.agent.config.deserializers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationDeserializer extends StdDeserializer<Duration> {
    public DurationDeserializer() {
        super(Duration.class);
    }

    private static final Pattern PATTERN = Pattern.compile("^([1-9][0-9]*|0)(ms|s|m|h)$");

    @Override
    public Duration deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final String text = p.readValueAs(String.class);

        final Matcher m = PATTERN.matcher(text);
        if (!m.matches())
            throw new IllegalArgumentException("invalid duration: " + text);

        final long value = Long.parseLong(m.group(1));
        final TemporalUnit unit;
        switch (m.group(2)) {
            case "ms":
                unit = ChronoUnit.MILLIS;
                break;
            case "s":
                unit = ChronoUnit.SECONDS;
                break;
            case "m":
                unit = ChronoUnit.MINUTES;
                break;
            case "h":
                unit = ChronoUnit.HOURS;
                break;
            default:
                throw new IllegalArgumentException("invalid duration: " + text);
        }

        return Duration.of(value, unit);
    }
}
