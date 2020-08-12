package net.thisptr.jmx.exporter.agent.handler.janino;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;

import net.thisptr.jmx.exporter.agent.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.PrometheusMetricOutput;
import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.jmx.exporter.agent.handler.ConditionScript;
import net.thisptr.jmx.exporter.agent.handler.Declarations;
import net.thisptr.jmx.exporter.agent.handler.ScriptEngine;
import net.thisptr.jmx.exporter.agent.handler.TransformScript;
import net.thisptr.jmx.exporter.agent.handler.janino.api.AttributeValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.handler.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.handler.janino.api._InternalUseDoNotImportProxyAccessor;
import net.thisptr.jmx.exporter.agent.handler.janino.api.fn.LogFunction;
import net.thisptr.jmx.exporter.agent.handler.janino.api.v1.V1;
import net.thisptr.jmx.exporter.agent.misc.Pair;

public class JaninoScriptEngine implements ScriptEngine {

	private static final String SCRIPT_HEADER = ""
			+ "import static " + LogFunction.class.getName() + ".*" + ";";

	private static final String SCRIPT_FOOTER = ""
			+ ";";

	public interface Transformer {
		void transform(AttributeValue in, MetricValueOutput out, Map<String, String> match) throws Exception;
	}

	private static class TransformScriptImpl implements TransformScript {
		private final Transformer transformer;

		public TransformScriptImpl(final Transformer transformer) {
			this.transformer = transformer;
		}

		@Override
		public void execute(final Sample<PrometheusScrapeRule> sample, final PrometheusMetricOutput output) {
			// We copy all the fields to decouple !java scripts and the rest of the code base.
			final AttributeValue in = new AttributeValue();
			in.attributeDescription = sample.attribute.getDescription();
			in.attributeName = sample.attribute.getName();
			in.attributeType = sample.attribute.getType();
			in.beanDescription = sample.info.getDescription();
			in.beanClass = sample.info.getClassName();
			in.domain = sample.name.domain();
			in.keyProperties = sample.name.keyProperties();
			in.timestamp = sample.timestamp;
			in.value = sample.value;
			try {
				transformer.transform(in, (m) -> {
					final PrometheusMetric metric = new PrometheusMetric();
					metric.name = m.name;
					metric.labels = m.labels;
					metric.value = m.value;
					metric.timestamp = m.timestamp;
					metric.help = m.help;
					metric.type = m.type;
					metric.suffix = m.suffix;
					metric.nameWriter = _InternalUseDoNotImportProxyAccessor.getNameWriter(m);
					output.emit(metric);
				}, sample.captures);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public interface ConditionExpression {
		boolean evaluate(final MBeanInfo mbean, final MBeanAttributeInfo attribute);
	}

	private static class ConditionScriptImpl implements ConditionScript {
		private final ConditionExpression expr;

		public ConditionScriptImpl(final ConditionExpression expr) {
			this.expr = expr;
		}

		@Override
		public boolean evaluate(final MBeanInfo mbean, final MBeanAttributeInfo attribute) {
			return expr.evaluate(mbean, attribute);
		}
	}

	private static class StaticBytecodeClassLoader extends ClassLoader {
		private final Map<String, byte[]> bytecodes;

		public StaticBytecodeClassLoader(final ClassLoader parent, final Map<String, byte[]> bytecodes) {
			super(parent);
			this.bytecodes = bytecodes;
		}

		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {
			final byte[] bytecode = bytecodes.get(name);
			if (bytecode == null)
				throw new ClassNotFoundException(name);
			return defineClass(name, bytecode, 0, bytecode.length);
		}
	}

	private static Pair<ClassLoader, StringBuilder> setupContext(final List<Declarations> declarations) {
		final List<String> staticImports = new ArrayList<>();
		final Map<String, byte[]> bytecodes = new HashMap<>();
		for (final Declarations decl : declarations) {
			if (decl instanceof JaninoDeclarations) {
				final JaninoDeclarations janinoDecl = (JaninoDeclarations) decl;
				bytecodes.putAll(janinoDecl.bytecodes);
				staticImports.add(janinoDecl.topLevelClassName);
			}
		}
		final ClassLoader classLoader = new StaticBytecodeClassLoader(JaninoScriptEngine.class.getClassLoader(), bytecodes);
		final StringBuilder script = new StringBuilder();
		for (String staticImport : staticImports)
			script.append(String.format("import static %s.*; ", staticImport));
		return Pair.of(classLoader, script);
	}

	@Override
	public ConditionScript compileConditionScript(final List<Declarations> declarations, final String text, final int ordinal) throws ScriptCompileException {
		try {
			final Pair<ClassLoader, StringBuilder> context = setupContext(declarations);
			final StringBuilder script = context._2;
			script.append(text);
			final ExpressionEvaluator ee = new ExpressionEvaluator();
			ee.setParentClassLoader(context._1);
			ee.setClassName("sjmxe.Rule" + ordinal + "Condition");
			final ConditionExpression expr = ee.createFastEvaluator(script.toString(), ConditionExpression.class, "mbeanInfo", "attributeInfo");
			return new ConditionScriptImpl(expr);
		} catch (final Exception e) {
			throw new ScriptCompileException(e);
		}
	}

	private class JaninoDeclarations implements Declarations {
		private final Map<String, byte[]> bytecodes;
		private final String topLevelClassName;

		public JaninoDeclarations(final String topLevelClassName, final Map<String, byte[]> bytesCodes) {
			this.topLevelClassName = topLevelClassName;
			this.bytecodes = bytesCodes;
		}
	}

	@Override
	public TransformScript compileTransformScript(final List<Declarations> declarations, final String text, final int ordinal) throws ScriptCompileException {
		final Pair<ClassLoader, StringBuilder> context = setupContext(declarations);
		final ScriptEvaluator se = new ScriptEvaluator();
		if (ordinal < 0) {
			se.setClassName("sjmxe.DefaultTransform");
		} else {
			se.setClassName("sjmxe.Rule" + ordinal + "Transform");
		}
		se.setDefaultImports(new String[] {
				AttributeValue.class.getName(),
				MetricValue.class.getName(),
				MetricValueOutput.class.getName(),
				V1.class.getName(),
		});
		se.setParentClassLoader(context._1);
		final StringBuilder script = context._2;
		script.append(SCRIPT_HEADER);
		script.append(text);
		script.append(SCRIPT_FOOTER);
		try {
			final Transformer compiledScript = (Transformer) se.createFastEvaluator(script.toString(), Transformer.class, new String[] { "in", "out", "match" });
			return new TransformScriptImpl(compiledScript);
		} catch (Exception e) {
			throw new ScriptCompileException(e);
		}
	}

	@Override
	public Declarations compileDeclarations(final String text, final int ordinal) throws ScriptCompileException {
		if (text == null)
			return null;
		final ClassBodyEvaluator evaluator = new ClassBodyEvaluator();
		try {
			final String topLevelClassName = "sjmxe.Declarations" + ordinal;
			evaluator.setClassName(topLevelClassName);
			evaluator.setDefaultImports(new String[] {
					AttributeValue.class.getName(),
					MetricValue.class.getName(),
					MetricValueOutput.class.getName(),
					V1.class.getName(),
			});
			evaluator.cook(text);
			return new JaninoDeclarations(topLevelClassName, evaluator.getBytecodes());
		} catch (final Exception e) {
			throw new ScriptCompileException(e);
		}
	}
}
