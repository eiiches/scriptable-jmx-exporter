package net.thisptr.jmx.exporter.agent.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class MoreClassesTest {

	@Test
	void testElementTypeNameOf() throws Exception {
		assertThat(MoreClasses.elementTypeNameOf("[J")).isEqualTo("long");
		assertThat(MoreClasses.elementTypeNameOf("[B")).isEqualTo("byte");
		assertThat(MoreClasses.elementTypeNameOf("[Z")).isEqualTo("boolean");
		assertThat(MoreClasses.elementTypeNameOf("[I")).isEqualTo("int");
		assertThat(MoreClasses.elementTypeNameOf("[S")).isEqualTo("short");
		assertThat(MoreClasses.elementTypeNameOf("[C")).isEqualTo("char");
		assertThat(MoreClasses.elementTypeNameOf("[F")).isEqualTo("float");
		assertThat(MoreClasses.elementTypeNameOf("[D")).isEqualTo("double");
		assertThat(MoreClasses.elementTypeNameOf("[[J")).isEqualTo("[J");
		assertThat(MoreClasses.elementTypeNameOf("[Ljava.lang.String;")).isEqualTo("java.lang.String");
	}

	@Test
	void testElementTypeNameOfInvalid() throws Exception {
		assertThatThrownBy(() -> MoreClasses.elementTypeNameOf("int")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> MoreClasses.elementTypeNameOf("[")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> MoreClasses.elementTypeNameOf("[A")).isInstanceOf(IllegalArgumentException.class);
	}
}
