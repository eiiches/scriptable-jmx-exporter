package net.thisptr.jmx.exporter.agent.utils;

import java.nio.charset.StandardCharsets;

public class MoreLongs {
	private final static byte[] DIGIT_TENS = new byte[100];
	private final static byte[] DIGIT_ONES = new byte[100];

	static {
		for (int i = 0; i < 10; ++i) {
			for (int j = 0; j < 10; ++j) {
				DIGIT_TENS[i * 10 + j] = (byte) (i + '0');
				DIGIT_ONES[i * 10 + j] = (byte) (j + '0');
			}
		}
	}

	private static final byte[] MIN_VALUE_BYTES = "-9223372036854775808".getBytes(StandardCharsets.UTF_8);

	/**
	 * @param x
	 * @param bytes
	 * @param index
	 * @return a new index
	 */
	public static int writeAsString(long x, final byte[] bytes, int index) {
		if (x < 0) {
			if (x == Long.MIN_VALUE) {
				System.arraycopy(MIN_VALUE_BYTES, 0, bytes, index, MIN_VALUE_BYTES.length);
				return index + MIN_VALUE_BYTES.length;
			}
			bytes[index++] = '-';
			x = -x;
		} else if (x == 0) {
			bytes[index++] = '0';
			return index;
		}

		index += MoreLongs.stringSize(x);
		final int newIndex = index;

		while (x >= 10) {
			final long q = x / 100; // HotSpot automatically optimizes this division into multiplication
			final int r = (int) (x - (q * 100));
			bytes[--index] = DIGIT_ONES[r];
			bytes[--index] = DIGIT_TENS[r];
			x = q;
		}

		if (x > 0) { // x2 < 10
			final byte r = (byte) (x + '0');
			bytes[--index] = r;
		}

		return newIndex;
	}

	/**
	 * Hand-optimized binary length search of given long value.
	 * 
	 * @param x 0 or positive long value
	 * @return
	 */
	public static int stringSize(final long x) {
		assert (x >= 0);
		if (x >= 1000000000L) { // >= 10
			if (x >= 100000000000000L) { // >= 15
				if (x >= 10000000000000000L) { // >= 17
					if (x >= 100000000000000000L) { // >= 18
						if (x >= 1000000000000000000L) {// >= 19
							return 19;
						} else {
							return 18;
						}
					} else { // < 18
						return 17;
					}
				} else { // < 17
					if (x >= 1000000000000000L) { // >= 16
						return 16;
					} else {
						return 15;
					}
				}
			} else { // 10 <= len(x) < 15
				if (x >= 100000000000L) { // >= 12
					if (x >= 1000000000000L) { // >= 13
						if (x >= 10000000000000L) { // >= 14
							return 14;
						} else { // < 14
							return 13;
						}
					} else { // < 13
						return 12;
					}
				} else { // < 12
					if (x >= 10000000000L) { // >= 11
						return 11;
					} else {
						return 10;
					}
				}
			}
		} else { // < 10
			if (x >= 10000L) { // >= 5
				if (x >= 1000000L) {// >= 7
					if (x >= 10000000L) { // >= 8
						if (x >= 100000000L) { // >= 9
							return 9;
						} else {
							return 8;
						}
					} else { // < 8
						return 7;
					}
				} else { // < 7
					if (x >= 100000L) {// >= 6
						return 6;
					} else {
						return 5;
					}
				}
			} else { // < 5
				if (x >= 100L) { // >= 3
					if (x >= 1000L) { // >= 4
						return 4;
					} else {
						return 3;
					}
				} else { // < 2
					if (x >= 10L) { // >= 2
						return 2;
					} else {
						return 1;
					}
				}
			}
		}
	}
}
