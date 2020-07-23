package net.thisptr.jmx.exporter.agent.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptEngineRegistry {
	private final Map<String, ScriptEngine<?>> engines = new ConcurrentHashMap<>();
	private ScriptEngine<?> defaultEngine;

	private static final ScriptEngineRegistry INSTANCE = new ScriptEngineRegistry();

	public static ScriptEngineRegistry getInstance() {
		return INSTANCE;
	}

	public void add(final String name, final ScriptEngine<?> engine) {
		engines.put(name, engine);
	}

	public ScriptEngine<?> get(final String name) {
		return engines.get(name);
	}

	public void setDefault(final String name) {
		final ScriptEngine<?> defaultEngine = engines.get(name);
		if (defaultEngine == null)
			throw new IllegalArgumentException("\" + name + \" is not registered");
		this.defaultEngine = defaultEngine;
	}

	public ScriptEngine<?> get() {
		return defaultEngine;
	}
}
