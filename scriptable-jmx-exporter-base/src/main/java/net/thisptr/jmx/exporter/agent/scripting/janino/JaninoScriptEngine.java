package net.thisptr.jmx.exporter.agent.scripting.janino;

import java.util.Map;
import java.util.regex.Pattern;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import net.thisptr.jmx.exporter.agent.scripting.FlightRecorderEventHandlerScript;
import net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr.FlightRecorderModule;
import net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr.FlightRecorderModuleImpl;
import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import net.thisptr.jmx.exporter.agent.registry.Registry;
import net.thisptr.jmx.exporter.agent.scraper.Sample;
import net.thisptr.jmx.exporter.agent.scripting.ConditionScript;
import net.thisptr.jmx.exporter.agent.scripting.ScriptContext;
import net.thisptr.jmx.exporter.agent.scripting.PrometheusMetric;
import net.thisptr.jmx.exporter.agent.scripting.PrometheusMetricOutput;
import net.thisptr.jmx.exporter.agent.scripting.ScriptEngine;
import net.thisptr.jmx.exporter.agent.scripting.TransformScript;
import net.thisptr.jmx.exporter.agent.scripting.janino.api.AttributeValue;
import net.thisptr.jmx.exporter.agent.scripting.janino.api.MetricValue;
import net.thisptr.jmx.exporter.agent.scripting.janino.api.MetricValueOutput;
import net.thisptr.jmx.exporter.agent.scripting.janino.api._InternalUseDoNotImportProxyAccessor;
import net.thisptr.jmx.exporter.agent.scripting.janino.api.fn.LogFunction;
import net.thisptr.jmx.exporter.agent.scripting.janino.api.v1.V1;

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
		public void execute(final Sample sample, final PrometheusMetricOutput output) {
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
		private final String code;

		public ConditionScriptImpl(final ConditionExpression expr, final String code) {
			this.expr = expr;
			this.code = code;
		}

		@Override
		public boolean evaluate(final MBeanInfo mbean, final MBeanAttributeInfo attribute) {
			return expr.evaluate(mbean, attribute);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((code == null) ? 0 : code.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ConditionScriptImpl other = (ConditionScriptImpl) obj;
			if (code == null) {
				if (other.code != null)
					return false;
			} else if (!code.equals(other.code))
				return false;
			return true;
		}
	}

	@Override
	public ConditionScript compileConditionScript(final ScriptContext context, final String text, final int ordinal) throws ScriptCompileException {
		final ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.setSourceVersion(JAVA_MAJOR_VERSION);
		ee.setTargetVersion(JAVA_MAJOR_VERSION);
		try {
			final StringBuilder script = new StringBuilder();
			for (final String className : context.declarationClassNames())
				script.append(String.format("import static %s.*; ", className));
			script.append(text);
			ee.setParentClassLoader(context.declarationClassLoader());
			ee.setClassName("sjmxe.Rule" + ordinal + "Condition");
			final ConditionExpression expr = ee.createFastEvaluator(script.toString(), ConditionExpression.class, "mbeanInfo", "attributeInfo");
			return new ConditionScriptImpl(expr, script.toString());
		} catch (final Exception e) {
			throw new ScriptCompileException(e);
		}
	}

	@Override
	public TransformScript compileTransformScript(final ScriptContext context, final String text, final int ordinal) throws ScriptCompileException {
		final ScriptEvaluator se = new ScriptEvaluator();
		se.setSourceVersion(JAVA_MAJOR_VERSION);
		se.setTargetVersion(JAVA_MAJOR_VERSION);
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
		se.setParentClassLoader(context.declarationClassLoader());
		final StringBuilder script = new StringBuilder();
		for (final String className : context.declarationClassNames())
			script.append(String.format("import static %s.*; ", className));
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

	private static final int JAVA_MAJOR_VERSION = javaMajorVersion();

	private static final int javaMajorVersion() {
		String version = System.getProperty("UnitCompiler.defaultTargetVersion");
		if (version != null) {
			return Integer.parseInt(version);
		}
		version = System.getProperty("java.specification.version");
		if (version == null || version.isEmpty()) {
			return -1; // let janino choose
		}
		final String[] components = version.split(Pattern.quote("."), -1);
		if ("1".equals(components[0])) {
			return Integer.parseInt(components[1]);
		} else {
			return Integer.parseInt(components[0]);
		}
	}

	@Override
	public void compileDeclarations(final ScriptContext context, final String text, final int ordinal) throws ScriptCompileException {
		if (text == null)
			return;
		final ClassBodyEvaluator evaluator = new ClassBodyEvaluator();
		evaluator.setSourceVersion(JAVA_MAJOR_VERSION);
		evaluator.setTargetVersion(JAVA_MAJOR_VERSION);
		try {
			final String topLevelClassName = "sjmxe.Declarations" + ordinal;
			evaluator.setClassName(topLevelClassName);
			evaluator.setDefaultImports(new String[] {
					AttributeValue.class.getName(),
					MetricValue.class.getName(),
					MetricValueOutput.class.getName(),
					V1.class.getName(),
					Timer.class.getName(),
					Counter.class.getName(),
					DistributionSummary.class.getName(),
					Registry.class.getName(),
			});
			evaluator.cook(text);
			context.addDeclarations(topLevelClassName, evaluator.getBytecodes());
		} catch (final Exception e) {
			throw new ScriptCompileException(e);
		}
	}

	@Override
	public FlightRecorderEventHandlerScript compileFlightRecorderEventHandlerScript(ScriptContext context, String handlerScript, int ordinal) throws ScriptCompileException {
		final ScriptEvaluator se = new ScriptEvaluator();
		se.setSourceVersion(JAVA_MAJOR_VERSION);
		se.setTargetVersion(JAVA_MAJOR_VERSION);
		se.setClassName("sjmxe.FlightRecorderEventHandler" + ordinal);
		se.setDefaultImports(new String[] {
				Timer.class.getName(),
				Counter.class.getName(),
				DistributionSummary.class.getName(),
				Registry.class.getName(),
		});
		se.setParentClassLoader(context.declarationClassLoader());
		final StringBuilder script = new StringBuilder();
		for (final String className : context.declarationClassNames())
			script.append(String.format("import static %s.*; ", className));
		script.append(SCRIPT_HEADER);
		script.append(handlerScript);
		script.append(SCRIPT_FOOTER);

		try {
			return FlightRecorderModule.getInstance().compile(se, script.toString());
		} catch (final Exception e) {
			throw new ScriptCompileException(e);
		}
	}
}
