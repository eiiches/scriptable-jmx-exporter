package net.thisptr.java.prometheus.metrics.agent;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.internal.javacc.JsonQueryParser;

public class RootScope {
	private static final Scope INSTANCE;
	static {
		INSTANCE = Scope.newEmptyScope();
		INSTANCE.loadFunctions(Scope.class.getClassLoader());
		try (Reader reader = new InputStreamReader(PrometheusExporterServer.class.getClassLoader().getResourceAsStream("prometheus.jq"))) {
			final List<String> lines = CharStreams.readLines(reader).stream()
					.filter(line -> !line.trim().startsWith("#"))
					.collect(Collectors.toList());
			lines.add("null");
			final JsonQuery jq = JsonQueryParser.compile(Joiner.on("\n").join(lines));
			jq.apply(INSTANCE, NullNode.getInstance());
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}

	public static Scope getInstance() {
		return INSTANCE;
	}
}