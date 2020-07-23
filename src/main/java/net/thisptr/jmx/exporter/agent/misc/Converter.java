package net.thisptr.jmx.exporter.agent.misc;

public interface Converter<T, U> {

	U convert(T t);
}
