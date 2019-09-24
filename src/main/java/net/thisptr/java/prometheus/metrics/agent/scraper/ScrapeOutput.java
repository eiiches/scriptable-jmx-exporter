package net.thisptr.java.prometheus.metrics.agent.scraper;

import net.thisptr.java.prometheus.metrics.agent.Sample;

public interface ScrapeOutput<ScrapeRuleType extends ScrapeRule> {

	void emit(Sample<ScrapeRuleType> sample);
}
