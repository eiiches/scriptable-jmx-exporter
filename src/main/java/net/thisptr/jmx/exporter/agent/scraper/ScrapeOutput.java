package net.thisptr.jmx.exporter.agent.scraper;

public interface ScrapeOutput {

	void emit(Sample sample);
}
