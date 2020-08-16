package net.thisptr.jmx.exporter.agent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thisptr.jmx.exporter.agent.metrics.Instrumented;
import net.thisptr.jmx.exporter.agent.scripting.PrometheusMetric;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildInfo implements Instrumented {
	private static final Logger LOG = Logger.getLogger(BuildInfo.class.getName());

	@JsonProperty("git.build.time")
	public Date buildTime;

	@JsonProperty("git.build.version")
	public String buildVersion = "N/A";

	@JsonProperty("git.commit.time")
	public Date commitTime;

	@JsonProperty("git.commit.id")
	public String commitId = "N/A";

	private static final BuildInfo INSTANCE;
	static {
		BuildInfo info = null;
		try (final InputStream is = BuildInfo.class.getClassLoader().getResourceAsStream("scriptable-jmx-exporter-git.json")) {
			if (is != null) {
				info = new ObjectMapper().readValue(is, BuildInfo.class);
			} else {
				LOG.log(Level.WARNING, "Could not obtain build information. scriptable-jmx-exporter-git.json is missing.");
			}
		} catch (final IOException e) {
			LOG.log(Level.WARNING, "Could not obtain build information.", e);
		}
		INSTANCE = info != null ? info : new BuildInfo();
	}

	public static BuildInfo getInstance() {
		return INSTANCE;
	}

	@Override
	public void toPrometheus(final Consumer<PrometheusMetric> fn) {
		final PrometheusMetric m = new PrometheusMetric();
		m.value = 1.0;
		m.name = "scriptable_jmx_exporter_build_info";
		m.labels = new HashMap<>();
		m.labels.put("version", buildVersion);
		m.labels.put("commit", commitId.substring(0, Math.min(7, commitId.length())));
		m.help = "Version information of Scriptable JMX Exporter";
		fn.accept(m);
	}
}
