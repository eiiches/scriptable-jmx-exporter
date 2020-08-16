package net.thisptr.jmx.exporter.agent;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.config.watcher.PollingConfigWatcher;
import net.thisptr.jmx.exporter.agent.utils.MoreValidators;

public class AgentTest {
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
		final PollingConfigWatcher watcher = new PollingConfigWatcher("@classpath:" + Agent.DEFAULT_CLASSPATH_CONFIG_FILE, (o, n) -> {});
		MoreValidators.validate(watcher.config());
	}
}
