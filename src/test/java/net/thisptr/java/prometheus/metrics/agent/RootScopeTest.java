package net.thisptr.java.prometheus.metrics.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Versions;

public class RootScopeTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JsonNode loadResourceJson(final String name) throws IOException {
		try (InputStream is = RootScopeTest.class.getClassLoader().getResourceAsStream(name)) {
			return MAPPER.readTree(is);
		}
	}

	private List<JsonNode> loadResourceJsonList(final String name) throws IOException {
		try (InputStream is = RootScopeTest.class.getClassLoader().getResourceAsStream(name)) {
			final List<JsonNode> out = new ArrayList<>();
			try (JsonParser p = MAPPER.getFactory().createParser(is)) {
				final MappingIterator<JsonNode> iter = MAPPER.readValues(p, JsonNode.class);
				while (iter.hasNext())
					out.add(iter.next());
			}
			return out;
		}
	}

	@Test
	void testDefaultTransformV1_NestedTabularData_1() throws Exception {
		final List<JsonNode> actual = new ArrayList<>();
		JsonQuery.compile("default_transform_v1", Versions.JQ_1_6).apply(RootScope.getInstance(), loadResourceJson("mbeans/nested_tabular_data.json"), actual::add);
		final List<JsonNode> expected = loadResourceJsonList("mbeans/nested_tabular_data.default_transform_v1.1.json");
		assertEquals(new HashSet<>(actual), new HashSet<>(expected));
	}

	@Test
	void testDefaultTransformV1_NestedTabularData_2() throws Exception {
		final List<JsonNode> actual = new ArrayList<>();
		JsonQuery.compile("default_transform_v1([\"type\"]; true)", Versions.JQ_1_6).apply(RootScope.getInstance(), loadResourceJson("mbeans/nested_tabular_data.json"), actual::add);
		final List<JsonNode> expected = loadResourceJsonList("mbeans/nested_tabular_data.default_transform_v1.2.json");
		assertEquals(new HashSet<>(actual), new HashSet<>(expected));
	}

	@Test
	void testDefaultTransformV1_NestedTabularData_3() throws Exception {
		final List<JsonNode> actual = new ArrayList<>();
		JsonQuery.compile("default_transform_v1([\"type\"]; true; {\"memoryUsageAfterGc_key1\": \"somekey\"})", Versions.JQ_1_6).apply(RootScope.getInstance(), loadResourceJson("mbeans/nested_tabular_data.json"), actual::add);
		final List<JsonNode> expected = loadResourceJsonList("mbeans/nested_tabular_data.default_transform_v1.3.json");
		assertEquals(new HashSet<>(actual), new HashSet<>(expected));
	}

	@Test
	void testDefaultTransformV1_NestedArrayOfTabularData_1() throws Exception {
		final List<JsonNode> actual = new ArrayList<>();
		JsonQuery.compile("default_transform_v1", Versions.JQ_1_6).apply(RootScope.getInstance(), loadResourceJson("mbeans/nested_array_of_tabular_data.json"), actual::add);
		final List<JsonNode> expected = loadResourceJsonList("mbeans/nested_array_of_tabular_data.default_transform_v1.1.json");
		assertEquals(new HashSet<>(actual), new HashSet<>(expected));
	}
}
