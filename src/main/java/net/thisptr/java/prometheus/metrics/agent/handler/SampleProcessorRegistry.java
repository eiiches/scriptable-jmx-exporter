package net.thisptr.java.prometheus.metrics.agent.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SampleProcessorRegistry {
	private final Map<String, SampleProcessor<?>> processors = new ConcurrentHashMap<>();
	private SampleProcessor<?> defaultProcessor;

	private static final SampleProcessorRegistry INSTANCE = new SampleProcessorRegistry();

	public static SampleProcessorRegistry getInstance() {
		return INSTANCE;
	}

	public void add(final String name, final SampleProcessor<?> processor) {
		processors.put(name, processor);
	}

	public SampleProcessor<?> get(final String name) {
		return processors.get(name);
	}

	public void setDefault(final String name) {
		final SampleProcessor<?> defaultProcessor = processors.get(name);
		if (defaultProcessor == null)
			throw new IllegalArgumentException("\" + name + \" is not registered");
		this.defaultProcessor = defaultProcessor;
	}

	public SampleProcessor<?> get() {
		return defaultProcessor;
	}
}
