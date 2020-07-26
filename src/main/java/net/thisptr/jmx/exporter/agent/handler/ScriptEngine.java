package net.thisptr.jmx.exporter.agent.handler;

public interface ScriptEngine {

	public static class ScriptCompileException extends Exception {
		private static final long serialVersionUID = 1L;

		public ScriptCompileException(String message, Throwable cause) {
			super(message, cause);
		}

		public ScriptCompileException(Throwable cause) {
			super(cause);
		}
	}

	TransformScript compileTransformScript(String script) throws ScriptCompileException;

	ConditionScript compileConditionScript(String script) throws ScriptCompileException;
}
