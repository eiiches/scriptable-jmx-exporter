package net.thisptr.jmx.exporter.agent.scripting;

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

	TransformScript compileTransformScript(ScriptContext context, String script, int ordinal) throws ScriptCompileException;

	ConditionScript compileConditionScript(ScriptContext context, String script, int ordinal) throws ScriptCompileException;

	void compileDeclarations(ScriptContext context, String text, int ordinal) throws ScriptCompileException;

	FlightRecorderEventHandlerScript compileFlightRecorderEventHandlerScript(ScriptContext context, String script, int ordinal) throws ScriptCompileException;
}
