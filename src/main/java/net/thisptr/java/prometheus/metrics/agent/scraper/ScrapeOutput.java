package net.thisptr.java.prometheus.metrics.agent.scraper;

import com.fasterxml.jackson.databind.JsonNode;

public interface ScrapeOutput<ScrapeRuleType extends ScrapeRule> {

	void emit(ScrapeRuleType rule, JsonNode object);
}
