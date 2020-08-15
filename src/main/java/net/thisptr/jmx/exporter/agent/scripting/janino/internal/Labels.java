package net.thisptr.jmx.exporter.agent.scripting.janino.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.google.common.collect.Maps;

import net.thisptr.jmx.exporter.agent.misc.MutableInteger;

public class Labels {
	private final Map<String, MutableInteger> counts;

	private final List<String> labels;
	private final List<String> values;
	private final List<Integer> dups;

	public Labels(final int expectedSize) {
		this.labels = new ArrayList<>(expectedSize);
		this.values = new ArrayList<>(expectedSize);
		this.dups = new ArrayList<>(expectedSize);
		this.counts = Maps.newHashMapWithExpectedSize(expectedSize);
	}

	public void push(final String label, final String value) {
		final int dup = counts.computeIfAbsent(label, (dummy) -> new MutableInteger()).getAndIncrement();
		if (dup == 0) {
			labels.add(label);
		} else {
			labels.add(label + "_" + dup);
		}
		values.add(value);
		dups.add(dup);
	}

	/**
	 * Removes the last pushed label-value pair. Must be invoked in reverse order of push().
	 * 
	 * @param label This must be the same label as given to the corresponding push().
	 */
	public void pop(final String label) {
		if (counts.get(label).decrementAndGet() <= 0)
			counts.remove(label);
		final int index = labels.size() - 1;
		labels.remove(index);
		values.remove(index);
	}

	public void forEach(final BiConsumer<String, String> fn) {
		for (int i = 0; i < labels.size(); ++i)
			fn.accept(labels.get(i), values.get(i));
	}

	public Map<String, String> toMapIfNotEmpty() {
		final int size = labels.size();
		if (size == 0)
			return null;
		final Map<String, String> map = Maps.newHashMapWithExpectedSize(size);
		for (int i = 0; i < size; ++i)
			map.put(labels.get(i), values.get(i));
		return map;
	}

	public int size() {
		return labels.size();
	}

	public void clear() {
		labels.clear();
		values.clear();
		dups.clear();
		counts.clear();
	}
}
