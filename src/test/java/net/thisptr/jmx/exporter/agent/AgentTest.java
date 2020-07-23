package net.thisptr.jmx.exporter.agent;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.ByteStreams;

import net.thisptr.jmx.exporter.agent.config.ClassPathPollingConfigWatcher;
import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.utils.MoreValidators;

public class AgentTest {
	private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

	static {
		// force execution of Agent.<clinit>
		try {
			Class.forName(Agent.class.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void testDefaultConfig() throws Exception {
		final byte[] bytes;
		try (InputStream is = ClassPathPollingConfigWatcher.class.getClassLoader().getResourceAsStream(Agent.DEFAULT_CLASSPATH_CONFIG_FILE)) {
			bytes = ByteStreams.toByteArray(is);
		}
		MoreValidators.validate(YAML_MAPPER.readValue(bytes, Config.class));
	}
}
