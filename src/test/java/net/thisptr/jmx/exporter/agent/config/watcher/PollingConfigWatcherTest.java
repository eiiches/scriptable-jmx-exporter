package net.thisptr.jmx.exporter.agent.config.watcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.config.watcher.PollingConfigWatcher.ConfigLoader;
import net.thisptr.jmx.exporter.agent.config.watcher.loaders.ClassPathConfigLoader;
import net.thisptr.jmx.exporter.agent.config.watcher.loaders.FileConfigLoader;
import net.thisptr.jmx.exporter.agent.config.watcher.loaders.StaticConfigLoader;

public class PollingConfigWatcherTest {
	@Test
	void testInstanciateConfigLoaders() throws Exception {
		final List<ConfigLoader> loaders = new ArrayList<>();
		final byte[] buf = "@foo.yaml,{\"foo\": true},@classpath:bar.yaml".getBytes();
		PollingConfigWatcher.instanciateConfigLoaders(buf, loaders::add);
		assertThat(loaders).hasSize(3);
		assertThat(loaders.get(0)).isInstanceOf(FileConfigLoader.class);
		assertThat(loaders.get(0).toString()).isEqualTo(new File("foo.yaml").getAbsolutePath());
		assertThat(loaders.get(1)).isInstanceOf(StaticConfigLoader.class);
		assertThat(loaders.get(1).toString()).isEqualTo("{\"foo\": true}");
		assertThat(loaders.get(2)).isInstanceOf(ClassPathConfigLoader.class);
		assertThat(loaders.get(2).toString()).isEqualTo("classpath:bar.yaml");
	}
}
