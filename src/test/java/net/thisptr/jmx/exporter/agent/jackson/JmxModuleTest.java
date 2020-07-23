package net.thisptr.jmx.exporter.agent.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JmxModuleTest {

	@Test
	void testSerializeTabularData() throws Exception {
		final String[] columns = new String[] { "key1", "key2", "val1", "val2" };
		final CompositeType rowType = new CompositeType("RowType", "RowType desc", columns,
				new String[] { "key1 desc", "key2 desc", "val1 desc", "val2 desc" },
				new OpenType[] { SimpleType.STRING, SimpleType.INTEGER, SimpleType.STRING, SimpleType.INTEGER });

		final TabularType tableType = new TabularType("TableType", "TableType desc", rowType, new String[] { "key1", "key2" });

		final TabularDataSupport table = new TabularDataSupport(tableType);

		table.put(new CompositeDataSupport(rowType, columns, new Object[] { "a", 1, "aa", 11 }));
		table.put(new CompositeDataSupport(rowType, columns, new Object[] { "b", 2, "bb", 22 }));

		final JsonNode actual = new ObjectMapper().registerModule(new JmxModule()).valueToTree(table);
		final JsonNode expected = new ObjectMapper()
				.readTree("{\"$type\":\"javax.management.openmbean.TabularData\",\"tabular_type\":{\"index_names\":[\"key1\",\"key2\"]},\"values\":[{\"$type\":\"javax.management.openmbean.CompositeData\",\"key1\":\"a\",\"key2\":1,\"val1\":\"aa\",\"val2\":11},{\"$type\":\"javax.management.openmbean.CompositeData\",\"key1\":\"b\",\"key2\":2,\"val1\":\"bb\",\"val2\":22}]}");
		assertEquals(expected, actual);
	}
}
