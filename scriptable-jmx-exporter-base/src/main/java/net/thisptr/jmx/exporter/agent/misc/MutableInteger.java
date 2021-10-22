package net.thisptr.jmx.exporter.agent.misc;

public class MutableInteger {
	private int value;

	public MutableInteger() {
		this(0);
	}

	public MutableInteger(final int value) {
		this.value = value;
	}

	public int decrementAndGet() {
		return --value;
	}

	public int get() {
		return value;
	}

	public void set(final int value) {
		this.value = value;
	}

	public int getAndIncrement() {
		return value++;
	}
}
