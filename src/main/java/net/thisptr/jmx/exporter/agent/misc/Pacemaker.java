package net.thisptr.jmx.exporter.agent.misc;

import java.time.Duration;

public class Pacemaker {

	public static final Duration DEFAULT_PRECISION = Duration.ofMillis(10);

	private int i = 0;

	private final int n;

	private final long durationNanos;

	private final long startNanos;

	private final long precisionNanos;

	public Pacemaker(Duration duration, int n) {
		this.startNanos = System.nanoTime();
		this.durationNanos = duration.toNanos();
		this.precisionNanos = DEFAULT_PRECISION.toNanos();
		this.n = n;
	}

	public void yield() throws InterruptedException {
		i++;
		final long waitUntilNanos = startNanos + (long) ((i / (double) n) * durationNanos);
		final long sleepNanos = waitUntilNanos - System.nanoTime();
		if (sleepNanos > precisionNanos) // sleep only when we are more than 10ms ahead, to avoid excessive context switches.
			sleepNanos(sleepNanos);
	}

	private static void sleepNanos(final long totalNanos) throws InterruptedException {
		final int nanos = (int) (totalNanos % 1000000L);
		final long millis = totalNanos / 1000000L;
		Thread.sleep(millis, nanos);
	}
}
