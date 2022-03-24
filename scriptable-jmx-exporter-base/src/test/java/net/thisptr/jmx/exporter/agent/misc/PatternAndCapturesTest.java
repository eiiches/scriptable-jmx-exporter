package net.thisptr.jmx.exporter.agent.misc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class PatternAndCapturesTest {

	@Test
	void testMatches() throws Exception {
		final PatternAndCaptures sut = PatternAndCaptures.compile("test (?<name>[a-z]+)");
		final Map<String, String> captures = new HashMap<>();
		assertThat(sut.matches("test foo")).isTrue();
		assertThat(sut.matches("test foo", captures)).isTrue();
		assertThat(captures).isEqualTo(Collections.singletonMap("name", "foo"));
	}

	@Test
	void testNotMatches() throws Exception {
		final PatternAndCaptures sut = PatternAndCaptures.compile("test (?<name>[a-z]+)");
		final Map<String, String> captures = new HashMap<>();
		assertThat(sut.matches("nomatch foo")).isFalse();
		assertThat(sut.matches("nomatch foo", captures)).isFalse();
		assertThat(captures).isEqualTo(Collections.emptyMap());
	}

	@Test
	void testAlternativeMatches() throws Exception {
		final PatternAndCaptures sut = PatternAndCaptures.compile("test ((?<name>[a-z]+)|(?<nums>[0-9]+))");
		final Map<String, String> captures = new HashMap<>();
		assertThat(sut.matches("test 123")).isTrue();
		assertThat(sut.matches("test 123", captures)).isTrue();
		assertThat(captures).isEqualTo(Collections.singletonMap("nums", "123"));
	}

	@Test
	void testEqualsAndHashCode() throws Exception {
		final PatternAndCaptures sut = PatternAndCaptures.compile("test (?<name>[a-z]+)");

		final PatternAndCaptures eq = PatternAndCaptures.compile("test (?<name>[a-z]+)");
		assertThat(sut).isEqualTo(eq);
		assertThat(sut.hashCode()).isEqualTo(eq.hashCode());

		final PatternAndCaptures neq = PatternAndCaptures.compile("test2 (?<name>[a-z]+)");
		assertThat(sut).isNotEqualTo(neq);
		assertThat(sut.hashCode()).isNotEqualTo(neq.hashCode());
	}
}
