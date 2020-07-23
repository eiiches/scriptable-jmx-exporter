package net.thisptr.jmx.exporter.agent.misc;

public interface StringWriter {
	/**
	 * @param name
	 * @return the *maximum* expected byte length when written
	 */
	int expectedSize(final String name);

	/**
	 * @param name
	 * @param bytes
	 * @param index
	 * @return a new index
	 */
	int write(final String name, final byte[] bytes, final int index);
}
