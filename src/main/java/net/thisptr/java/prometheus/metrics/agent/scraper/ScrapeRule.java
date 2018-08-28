package net.thisptr.java.prometheus.metrics.agent.scraper;

import java.util.List;

import net.thisptr.java.prometheus.metrics.agent.misc.AttributeNamePattern;

public interface ScrapeRule {

	List<AttributeNamePattern> patterns();

	boolean skip();
}
