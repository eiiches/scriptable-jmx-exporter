package net.thisptr.jmx.exporter.agent.jackson.serdes;

import java.io.IOException;

import javax.management.openmbean.CompositeData;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CompositeDataSerializer extends StdSerializer<CompositeData> {
	private static final long serialVersionUID = 9185892883956517645L;

	public CompositeDataSerializer() {
		super(CompositeData.class);
	}

	@Override
	public void serialize(final CompositeData data, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("$type", "javax.management.openmbean.CompositeData");
		for (final String key : data.getCompositeType().keySet())
			gen.writeObjectField(key, data.get(key));
		gen.writeEndObject();
	}
}
