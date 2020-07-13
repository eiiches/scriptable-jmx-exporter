package net.thisptr.java.prometheus.metrics.agent.handler.janino;

import javax.management.ObjectName;

import org.codehaus.janino.ScriptEvaluator;

import net.thisptr.java.prometheus.metrics.agent.PrometheusMetric;
import net.thisptr.java.prometheus.metrics.agent.PrometheusMetricOutput;
import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.config.Config.PrometheusScrapeRule;
import net.thisptr.java.prometheus.metrics.agent.handler.ScriptEngine;
import net.thisptr.java.prometheus.metrics.agent.handler.Script;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.JaninoScriptEngine.Transformer;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.functions.TransformV1Function;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.AttributeValue;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.MetricValue;
import net.thisptr.java.prometheus.metrics.agent.handler.janino.iface.MetricValueOutput;

public class JaninoScriptEngine implements ScriptEngine<Transformer> {

	private static final String SCRIPT_HEADER = ""
			+ "import static " + TransformV1Function.class.getName() + ".*" + ";";

	private static final String SCRIPT_FOOTER = ""
			+ ";";

	public interface Transformer {
		void transform(AttributeValue in, MetricValueOutput out) throws Exception;
	}

	@Override
	public Script<Transformer> compile(final String script) throws ScriptCompileException {
		final ScriptEvaluator se = new ScriptEvaluator();
		se.setDefaultImports(new String[] {
				ObjectName.class.getName(),
				AttributeValue.class.getName(),
				MetricValue.class.getName(),
				MetricValueOutput.class.getName(),
		});
		try {
			final Transformer compiledScript = (Transformer) se.createFastEvaluator(SCRIPT_HEADER + script + SCRIPT_FOOTER, Transformer.class, new String[] { "in", "out" });
			return new Script<>(this, compiledScript);
		} catch (Exception e) {
			throw new ScriptCompileException(e);
		}
	}

	@Override
	public void handle(final Sample<PrometheusScrapeRule> sample, final Transformer script, final PrometheusMetricOutput output) {
		// We copy all the fields to decouple !java scripts and the rest of the code base.
		final AttributeValue in = new AttributeValue();
		in.attributeInfo = sample.attribute;
		in.mbeanInfo = sample.info;
		in.name = sample.name;
		in.timestamp = sample.timestamp;
		in.value = sample.value;
		try {
			script.transform(in, (m) -> {
				final PrometheusMetric metric = new PrometheusMetric();
				metric.name = m.name;
				metric.labels = m.labels;
				metric.value = m.value;
				metric.timestamp = m.timestamp;
				metric.help = m.help;
				metric.type = m.type;
				output.emit(metric);
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
