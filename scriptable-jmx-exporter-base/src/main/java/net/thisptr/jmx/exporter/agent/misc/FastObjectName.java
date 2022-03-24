package net.thisptr.jmx.exporter.agent.misc;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.ObjectName;

/**
 * ObjectName copies a lot of objects when calling its methods. This class is a wrapper that pre-computes frequently used values.
 */
public class FastObjectName {
	private final String domain;
	private final Map<String, String> keyProperties;
	private final ObjectName objectName;

	public FastObjectName(final ObjectName objectName) {
		this.domain = objectName.getDomain();
		final Hashtable<String, String> originalKeyProperties = objectName.getKeyPropertyList();
		// LinkedHashMap is faster to iterate, while insertion is slower. As we iterate this map so many times, LinkedHashMap should be used.
		final Map<String, String> keyProperties = new LinkedHashMap<>(originalKeyProperties.size());
		originalKeyProperties.forEach((k, v) -> {
			if (v.startsWith("\""))
				v = ObjectName.unquote(v);
			keyProperties.put(k, v);
		});
		this.keyProperties = Collections.unmodifiableMap(keyProperties);
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
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FastObjectName other = (FastObjectName) obj;
		return objectName.equals(other.objectName);
	}

	@Override
	public int hashCode() {
		return objectName.hashCode();
	}

	@Override
	public String toString() {
		// TODO: should we cache this?
		return objectName.toString();
	}
}
