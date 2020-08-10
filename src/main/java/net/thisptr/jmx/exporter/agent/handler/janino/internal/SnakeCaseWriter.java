package net.thisptr.jmx.exporter.agent.handler.janino.internal;

import net.thisptr.jmx.exporter.agent.misc.StringWriter;

public class SnakeCaseWriter implements StringWriter {
	private static final SnakeCaseWriter INSTANCE = new SnakeCaseWriter();

	public static SnakeCaseWriter getInstance() {
		return INSTANCE;
	}

	@Override
	public int expectedSize(final String name) {
		// worst case: all CAPITAL -> c_a_p_i_t_a_l
		return Math.max(1, name.length() * 2);
	}

	private static class CharactorGroup {
		private static final byte encode(final boolean subsequentLeadingCapitalNeedsUnderscore, final boolean subsequentCapitalNeedsUnderscore) {
			byte val = 0;
			if (subsequentLeadingCapitalNeedsUnderscore)
				val |= 0b10;
			if (subsequentCapitalNeedsUnderscore)
				val |= 0b01;
			return val;
		}

		private static final byte LOWER_CASE_ALPHABET = encode(true, true);
		private static final byte UPPER_CASE_ALPHABET = encode(true, false);
		private static final byte NUMBER = encode(true, true);
		private static final byte COLON_OR_UNDERSCORE = encode(false, false);
		private static final byte OTHER = encode(false, false);
		private static final byte INITIAL = encode(false, false);

		// Leading capital is a upper-case alphabet that is immediately followed by a lower-case letter, for example,
		// * F in "Foo"
		// * C in "SSLCertificate"
		public static final boolean subsequentLeadingCapitalNeedsUnderscore(final byte type) {
			return (type & 0b10) > 0;
		}

		public static final boolean subsequentCapitalNeedsUnderscore(final byte type) {
			return (type & 0b01) > 0;
		}
	}

	@Override
	public int write(final String name, final byte[] bytes, int index) {
		final int savedIndex = index;
		int length = name.length();
		byte pcharType = CharactorGroup.INITIAL;
		for (int i = 0; i < length; ++i) {
			final char ch = name.charAt(i);
			if ('a' <= ch && ch <= 'z') {
				bytes[index++] = (byte) ch;
				pcharType = CharactorGroup.LOWER_CASE_ALPHABET;
			} else if ('A' <= ch && ch <= 'Z') {
				final char nchar = i + 1 < length ? name.charAt(i + 1) : 0;
				if ('a' <= nchar && nchar <= 'z') {
					if (CharactorGroup.subsequentLeadingCapitalNeedsUnderscore(pcharType))
						bytes[index++] = '_';
				} else { // nchar = [^a-z]
					if (CharactorGroup.subsequentCapitalNeedsUnderscore(pcharType))
						bytes[index++] = '_';
				}
				bytes[index++] = (byte) (ch + ('a' - 'A'));
				pcharType = CharactorGroup.UPPER_CASE_ALPHABET;
			} else if (ch == ':' || ch == '_') {
				bytes[index++] = (byte) ch;
				pcharType = CharactorGroup.COLON_OR_UNDERSCORE;
			} else if ('0' <= ch && ch <= '9') {
				if (savedIndex == index) {
					bytes[index++] = '_'; // first char cannot be a number; prepend _;
				}
				bytes[index++] = (byte) ch;
				pcharType = CharactorGroup.NUMBER;
			} else {
				if (Character.isHighSurrogate(ch))
					++i;
				bytes[index++] = '_';
				pcharType = CharactorGroup.OTHER;
			}
		}
		if (savedIndex == index) { // empty metric name is not allowed
			bytes[index++] = '_';
		}
		return index;
	}
}
