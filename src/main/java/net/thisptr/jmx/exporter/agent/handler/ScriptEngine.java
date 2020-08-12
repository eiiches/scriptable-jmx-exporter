package net.thisptr.jmx.exporter.agent.handler;

import java.util.List;

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

	TransformScript compileTransformScript(List<Declarations> declarations, String script, int ordinal) throws ScriptCompileException;

	ConditionScript compileConditionScript(List<Declarations> declarations, String script, int ordinal) throws ScriptCompileException;

	Declarations compileDeclarations(String text, int ordinal) throws ScriptCompileException;
}
