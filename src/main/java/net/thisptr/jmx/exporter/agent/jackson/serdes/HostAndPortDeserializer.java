package net.thisptr.jmx.exporter.agent.jackson.serdes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.net.HostAndPort;

public class HostAndPortDeserializer extends StdDeserializer<HostAndPort> {
	private static final long serialVersionUID = -6301194092126572385L;

	public HostAndPortDeserializer() {
		super(HostAndPort.class);
	}

	@Override
	public HostAndPort deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final String hostAndPortString = p.readValueAs(String.class);
		if (hostAndPortString == null)
			return null;
		return HostAndPort.fromString(hostAndPortString);
	}
}
