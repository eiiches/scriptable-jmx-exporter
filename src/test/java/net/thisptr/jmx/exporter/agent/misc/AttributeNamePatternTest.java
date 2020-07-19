package net.thisptr.jmx.exporter.agent.misc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.management.ObjectName;

import org.junit.jupiter.api.Test;

public class AttributeNamePatternTest {

	@Test
	void testMatchesAny() throws Exception {
		final AttributeNamePattern p = AttributeNamePattern.compile("::");
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector,name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "First"));
		assertTrue(p.matches(new ObjectName("java.langa:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector2"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), null));
	}

	@Test
	void testMatchesAttribute() throws Exception {
		final AttributeNamePattern p = AttributeNamePattern.compile("::LastGcInfo");
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector,name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "First"));
		assertTrue(p.matches(new ObjectName("java.langa:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector2"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=GarbageCollector"), null));
	}

	@Test
	void testMatchesPropAndAttribute() throws Exception {
		final AttributeNamePattern p = AttributeNamePattern.compile(":type=GarbageCollector:LastGcInfo");
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector,name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "First"));
		assertTrue(p.matches(new ObjectName("java.langa:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=GarbageCollector2"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=GarbageCollector"), null));
	}

	@Test
	void testMatches1() throws Exception {
		final AttributeNamePattern p = AttributeNamePattern.compile("java\\\\.lang");
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector,name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "First"));
		assertFalse(p.matches(new ObjectName("java.langa:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector2"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), null));
	}

	@Test
	void testMatches2a() throws Exception {
		final AttributeNamePattern p = AttributeNamePattern.compile("java\\\\.lang:type=GarbageCollector");
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector,name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "First"));
		assertFalse(p.matches(new ObjectName("java.langa:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=GarbageCollector2"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), null));
	}

	@Test
	void testMatches2b() throws Exception {
		final AttributeNamePattern p = AttributeNamePattern.compile("java\\\\.lang:type=GarbageCollector:");
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector,name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "First"));
		assertFalse(p.matches(new ObjectName("java.langa:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=GarbageCollector2"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), null));
	}

	@Test
	void testMatches3() throws Exception {
		final AttributeNamePattern p = AttributeNamePattern.compile("java\\\\.lang:type=GarbageCollector:Last.*");
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=GarbageCollector,name=Foo"), "LastGcInfo"));
		assertTrue(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=\"GarbageCollector\",name=Foo"), "First"));
		assertFalse(p.matches(new ObjectName("java.langa:type=\"GarbageCollector\",name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:name=Foo"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=GarbageCollector2"), "LastGcInfo"));
		assertFalse(p.matches(new ObjectName("java.lang:type=GarbageCollector"), null));
	}
}
