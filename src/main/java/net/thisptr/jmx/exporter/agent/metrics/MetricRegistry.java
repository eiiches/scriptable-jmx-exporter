package net.thisptr.jmx.exporter.agent.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This registry is for exposing exporter metrics.
 */
public class MetricRegistry {

	public List<Instrumented> instrumentedObjects = new ArrayList<>();

	public void forEach(final Consumer<Instrumented> fn) {
		instrumentedObjects.forEach(fn);
	}

	public void add(final Instrumented instrumented) {
		instrumentedObjects.add(instrumented);
	}
}
