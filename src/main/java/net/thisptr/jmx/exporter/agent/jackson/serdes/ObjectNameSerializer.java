package net.thisptr.jmx.exporter.agent.jackson.serdes;

import java.io.IOException;

import javax.management.ObjectName;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ObjectNameSerializer extends StdSerializer<ObjectName> {
	private static final long serialVersionUID = 7409998425409095249L;

	public ObjectNameSerializer() {
		super(ObjectName.class);
	}

	@Override
	public void serialize(final ObjectName value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
		gen.writeString(value.toString());
	}
}
