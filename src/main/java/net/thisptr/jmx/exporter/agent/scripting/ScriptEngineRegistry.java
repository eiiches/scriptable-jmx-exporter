package net.thisptr.jmx.exporter.agent.scripting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptEngineRegistry {
	private final Map<String, ScriptEngine> engines = new ConcurrentHashMap<>();

	private static final ScriptEngineRegistry INSTANCE = new ScriptEngineRegistry();

	public static ScriptEngineRegistry getInstance() {
		return INSTANCE;
	}

	public void add(final String name, final ScriptEngine engine) {
		engines.put(name, engine);
	}

	public ScriptEngine get(final String name) {
		final ScriptEngine engine = engines.get(name);
		if (engine == null)
			throw new IllegalArgumentException(String.format("\"%s\" is not registered", name));
		return engine;
	}
}
