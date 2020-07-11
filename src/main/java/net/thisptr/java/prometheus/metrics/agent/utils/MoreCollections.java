package net.thisptr.java.prometheus.metrics.agent.utils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MoreCollections {

	/**
	 * Like forEach, but visits each element slowly over the given duration.
	 *
	 * @param <T>
	 * @param elements
	 * @param duration
	 * @param unit
	 * @param fn
	 * @throws InterruptedException
	 */
	public static <T> void forEachSlowlyOverDuration(final List<T> elements, final long duration, final TimeUnit unit, final Consumer<T> fn) throws InterruptedException {
		final long startNanos = System.nanoTime();
		final long durationNanos = unit.toNanos(duration);
		for (int i = 0; i < elements.size(); ++i) {
			final long waitUntilNanos = startNanos + (long) (((i + 1) / (double) elements.size()) * durationNanos);
			final long sleepNanos = waitUntilNanos - System.nanoTime();
			if (sleepNanos > 10_000_000) // sleep only when we are more than 10ms ahead, to avoid excessive context switches.
				sleepNanos(sleepNanos);
			fn.accept(elements.get(i));
		}
		// The previous loop can finish at most 10ms earlier than desired.
		final long waitUntilNanos = startNanos + durationNanos;
		final long sleepNanos = waitUntilNanos - System.nanoTime();
		if (sleepNanos > 0)
			sleepNanos(sleepNanos);
	}

	private static void sleepNanos(final long totalNanos) throws InterruptedException {
		final int nanos = (int) (totalNanos % 1000000L);
		final long millis = totalNanos / 1000000L;
		Thread.sleep(millis, nanos);
	}
}
