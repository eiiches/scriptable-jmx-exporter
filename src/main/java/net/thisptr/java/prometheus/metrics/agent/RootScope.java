package net.thisptr.java.prometheus.metrics.agent;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;

import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.Expression;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.internal.javacc.ExpressionParser;
import net.thisptr.java.prometheus.metrics.agent.handler.jq.functions.DefaultTransformV1Function;
import net.thisptr.java.prometheus.metrics.agent.handler.jq.functions.JmxFunction;

public class RootScope {
	private static final Scope INSTANCE;
	static {
		INSTANCE = Scope.newEmptyScope();
		BuiltinFunctionLoader.getInstance().loadFunctions(Versions.JQ_1_6, INSTANCE);
		try (Reader reader = new InputStreamReader(RootScope.class.getClassLoader().getResourceAsStream("prometheus.jq"))) {
			final List<String> lines = CharStreams.readLines(reader).stream()
					.filter(line -> !line.trim().startsWith("#"))
					.collect(Collectors.toList());
			lines.add("null");
			final Expression jq = ExpressionParser.compile(Joiner.on("\n").join(lines), Versions.JQ_1_6);
			jq.apply(INSTANCE, NullNode.getInstance(), (out) -> {});
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
		INSTANCE.addFunction("jmx", 2, new JmxFunction());
		INSTANCE.addFunction("default_transform_v1", 0, new DefaultTransformV1Function());
		INSTANCE.addFunction("default_transform_v1", 2, new DefaultTransformV1Function());
	}

	public static Scope getInstance() {
		return INSTANCE;
	}
}