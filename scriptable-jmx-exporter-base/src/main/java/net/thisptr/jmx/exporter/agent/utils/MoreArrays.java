package net.thisptr.jmx.exporter.agent.utils;

public class MoreArrays {

	/**
	 * Use this only when array length is small enough for linear search.
	 * 
	 * @param set
	 * @param value
	 * @return
	 */
	public static boolean contains(final String[] set, final String value) {
		// This logic is O(N), but is justified as N is usually 1 or 2.
		for (final String elementInSet : set)
			if (elementInSet.equals(value))
				return true;
		return false;
	}

}
