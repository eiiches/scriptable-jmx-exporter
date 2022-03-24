package net.thisptr.jmx.exporter.agent.scripting.janino.internal;

import net.thisptr.jmx.exporter.agent.misc.StringWriter;

public class LowerCaseWriter implements StringWriter {
	private static final LowerCaseWriter INSTANCE = new LowerCaseWriter();

	public static LowerCaseWriter getInstance() {
		return INSTANCE;
	}

	@Override
	public int expectedSize(final String name) {
		return name.length() + 1;
	}

	@Override
	public int write(final String name, final byte[] bytes, int index) {
		final int savedIndex = index;
		int length = name.length();
		for (int i = 0; i < length; ++i) {
			final char ch = name.charAt(i);
			if ('a' <= ch && ch <= 'z') {
				bytes[index++] = (byte) ch;
			} else if ('A' <= ch && ch <= 'Z') {
				bytes[index++] = (byte) (ch + ('a' - 'A'));
			} else if (ch == '_' || ch == ':') {
				bytes[index++] = (byte) ch;
			} else if ('0' <= ch && ch <= '9') {
				if (savedIndex == index)
					bytes[index++] = '_'; // first char cannot be a number; prepend _;
				bytes[index++] = (byte) ch;
			} else {
				if (Character.isHighSurrogate(ch))
					++i;
				bytes[index++] = '_';
			}
		}
		if (savedIndex == index) { // empty metric name is not allowed
			bytes[index++] = '_';
		}
		return index;
	}
}
