package net.thisptr.jmx.exporter.agent.scraper;

import java.util.List;

import net.thisptr.jmx.exporter.agent.handler.ConditionScript;
import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;

public interface ScrapeRule {

	List<AttributeNamePattern> patterns();

	boolean skip();

	ConditionScript condition();
}
