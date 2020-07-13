package net.thisptr.java.prometheus.metrics.agent.handler.janino.iface;

import java.util.Map;

public class MetricValue {
	public String name;
	public Map<String, String> labels;

	public double value;
	public long timestamp = 0;

	public String help;
	public String type;
}