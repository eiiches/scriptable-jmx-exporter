package net.thisptr.java.prometheus.metrics.agent.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import net.thisptr.java.prometheus.metrics.agent.utils.MoreValidators;

public class ConfigTest {

	@Test
	void testDefaultIsValid() throws Exception {
		final Config sut = new Config();
		MoreValidators.validate(sut);
	}

	@Test
	void testNullsInRules() throws Exception {
		final Config sut = new Config();
		sut.rules.add(null);
		assertThrows(ValidationException.class, () -> MoreValidators.validate(sut));
	}

	@Test
	void testNullBindAddress() throws Exception {
		final Config sut = new Config();
		sut.server.bindAddress = null;
		assertThrows(ValidationException.class, () -> MoreValidators.validate(sut));
	}
}
