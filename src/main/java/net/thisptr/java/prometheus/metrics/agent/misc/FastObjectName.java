package net.thisptr.java.prometheus.metrics.agent.misc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

/**
 * ObjectName copies a lot of objects when calling its methods. This class is a wrapper that pre-computes necessary values.
 * This class does not implement {@link #hashCode()} and {@link #equals(Object)}.
 */
public class FastObjectName {
	private final String domain;
	private final Map<String, String> keyProperties;
	private final ObjectName objectName;

	public FastObjectName(final ObjectName objectName) {
		this.domain = objectName.getDomain();
		this.keyProperties = Collections.unmodifiableMap(new HashMap<>(objectName.getKeyPropertyList()));
		this.objectName = objectName;
	}

	public String domain() {
		return domain;
	}

	/**
	 * @return an unmodifiable key properties.
	 */
	public Map<String, String> keyProperties() {
		return keyProperties;
	}

	public ObjectName objectName() {
		return objectName;
	}

	@Override
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String toString() {
		// TODO: cache
		return objectName.toString();
	}
}