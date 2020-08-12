package net.thisptr.jmx.exporter.agent.scraper;

import net.thisptr.jmx.exporter.agent.Sample;

public interface ScrapeOutput {

	void emit(Sample sample);
}
