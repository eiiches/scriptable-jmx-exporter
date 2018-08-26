package net.thisptr.java.prometheus.metrics.agent.jackson.serdes;

import java.io.IOException;

import javax.management.openmbean.TabularData;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class TabularDataSerializer extends StdSerializer<TabularData> {
	private static final long serialVersionUID = -8591576343905385107L;

	public TabularDataSerializer() {
		super(TabularData.class);
	}

	@Override
	public void serialize(final TabularData data, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
		gen.writeStartObject();

		gen.writeStringField("$type", "javax.management.openmbean.TabularData");

		gen.writeFieldName("tabular_type");
		gen.writeStartObject();
		gen.writeObjectField("index_names", data.getTabularType().getIndexNames());
		gen.writeEndObject();

		gen.writeFieldName("values");
		gen.writeStartArray();
		for (final Object value : data.values())
			gen.writeObject(value);
		gen.writeEndArray();

		gen.writeEndObject();
	}
}
