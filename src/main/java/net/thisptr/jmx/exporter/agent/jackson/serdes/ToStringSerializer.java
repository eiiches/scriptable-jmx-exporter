package net.thisptr.jmx.exporter.agent.jackson.serdes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ToStringSerializer extends StdSerializer<Object> {
	private static final long serialVersionUID = -3355464005202101593L;

	public ToStringSerializer() {
		super(Object.class);
	}

	@Override
	public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
		if (value == null)
			gen.writeNull();
		gen.writeString(value.toString());
	}
}