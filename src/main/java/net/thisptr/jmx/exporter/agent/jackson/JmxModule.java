package net.thisptr.jmx.exporter.agent.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import net.thisptr.jmx.exporter.agent.jackson.serdes.CompositeDataSerializer;
import net.thisptr.jmx.exporter.agent.jackson.serdes.ObjectNameSerializer;
import net.thisptr.jmx.exporter.agent.jackson.serdes.TabularDataSerializer;

public class JmxModule extends SimpleModule {
	private static final long serialVersionUID = 8672321899402795168L;

	public JmxModule() {
		addSerializer(new CompositeDataSerializer());
		addSerializer(new TabularDataSerializer());
		addSerializer(new ObjectNameSerializer());
	}
}
