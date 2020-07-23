package net.thisptr.jmx.exporter.agent.scraper;

import net.thisptr.jmx.exporter.agent.Sample;

public interface ScrapeOutput<ScrapeRuleType extends ScrapeRule> {

	void emit(Sample<ScrapeRuleType> sample);
}
