package net.thisptr.java.prometheus.metrics.agent.handler.janino;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

import org.codehaus.janino.ScriptEvaluator;

import net.thisptr.java.prometheus.metrics.agent.PrometheusMetricOutput;
import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.handler.SampleProcessor;
import net.thisptr.java.prometheus.metrics.agent.handler.Script;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.JaninoSampleProcessor.JaninoScript;

public class JaninoSampleProcessor implements SampleProcessor<JaninoScript> {

	private static final String SCRIPT_HEADER = ""
			+ "import static " + DefaultTransformV1Function.class.getName() + ".*" + ";";

	private static final String SCRIPT_FOOTER = ""
			+ ";";

	public static class Input {
		public ObjectName name;
		public MBeanInfo mbeanInfo;
		public MBeanAttributeInfo attributeInfo;

		public long timestamp;
		public Object value;
	}

	public static class Output {

	}

	public interface JaninoScript {
		void execute(Input in, PrometheusMetricOutput out) throws Exception;
	}

	@Override
	public Script<JaninoScript> compile(final String script) throws ScriptCompileException {
		final ScriptEvaluator se = new ScriptEvaluator();
		// se.setParameters(new String[] { "in", "out" }, new Class[] { Input.class, PrometheusMetricOutput.class });
		// se.setImplementedInterfaces(new Class[] { JaninoScript.class });
		// se.setMethodName("execute");
		try {
			// se.cook(SCRIPT_HEADER + script + SCRIPT_FOOTER);
			// final JaninoScript compiledScript = ((JaninoScript) se.getClazz().getDeclaredConstructor().newInstance());
			final JaninoScript compiledScript2 = (JaninoScript) se.createFastEvaluator(SCRIPT_HEADER + script + SCRIPT_FOOTER, JaninoScript.class, new String[] { "in", "out" });
			return new Script<>(this, compiledScript2);
		} catch (Exception e) {
			throw new ScriptCompileException(e);
		}
	}

	@Override
	public void handle(final Sample<PrometheusScrapeRule> sample, final JaninoScript script, final PrometheusMetricOutput output) {
		final Input in = new Input();
		in.attributeInfo = sample.attribute;
		in.mbeanInfo = sample.info;
		in.name = sample.name;
		in.timestamp = sample.timestamp;
		in.value = sample.value;
		try {
			script.execute(in, output);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
