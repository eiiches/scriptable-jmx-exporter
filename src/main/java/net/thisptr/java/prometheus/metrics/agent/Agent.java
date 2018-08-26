package net.thisptr.java.prometheus.metrics.agent;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import fi.iki.elonen.NanoHTTPD;

public class Agent {
	private static final Logger LOG = Logger.getLogger(Agent.class.getName());
	private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

	private static Config loadConfig(final String args) throws JsonParseException, JsonMappingException, IOException {
		if (args == null || args.isEmpty()) {
			return new Config();
		} else if (args.startsWith("@")) {
			return MAPPER.readValue(new File(args.substring(1)), Config.class);
		} else {
			return MAPPER.readValue(args, Config.class);
		}
	}

	private static void validateConfig(final Config config) {
		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			final Validator validator = validatorFactory.getValidator();
			final Set<ConstraintViolation<Config>> violations = validator.validate(config);
			if (!violations.isEmpty())
				throw new ConstraintViolationException(violations);
		}
	}

	public static void premain(final String args) throws Throwable {
		try {
			final Config config = loadConfig(args);
			validateConfig(config);

			final PrometheusExporterServer server = new PrometheusExporterServer(config);
			server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
		} catch (final Throwable th) {
			LOG.log(Level.SEVERE, "Failed to start Prometheus exporter.", th);
			throw th;
		}
	}
}
