package net.thisptr.jmx.exporter.agent.config;

public class ScriptText {
	public final String engineName;
	public final String scriptBody;

	public ScriptText(final String engineName, final String scriptBody) {
		this.engineName = engineName;
		this.scriptBody = scriptBody;
	}

	public static ScriptText valueOf(String text) {
		if (text == null)
			return null;
		text = text.trim();
		if (text.startsWith("!")) {
			int i = 1;
			for (; i < text.length(); ++i) {
				final int ch = text.charAt(i);
				if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z')
					continue;
				break;
			}
			final String engineName = text.substring(1, i);
			final String scriptBody = text.substring(i);
			return new ScriptText(engineName, scriptBody);
		} else {
			return new ScriptText(null, text);
		}
	}
}
