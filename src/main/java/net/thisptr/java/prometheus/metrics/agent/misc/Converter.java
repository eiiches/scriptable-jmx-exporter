package net.thisptr.java.prometheus.metrics.agent.misc;

public interface Converter<T, U> {

	U convert(T t);
}
