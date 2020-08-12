package net.thisptr.jmx.exporter.agent.scraper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import net.thisptr.jmx.exporter.agent.Sample;
import net.thisptr.jmx.exporter.agent.config.Config.ScrapeRule;
import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;

public class ScraperTest {

	private static ScrapeRule newScrapeRule(final String pattern, final boolean skip) {
		final ScrapeRule rule = new ScrapeRule();
		if (pattern != null) {
			rule.patterns = Arrays.asList(AttributeNamePattern.compile(pattern));
		}
		rule.skip = skip;
		return rule;
	}

	private static final ScrapeRule DEFAULT_INCLUSION_RULE = newScrapeRule(null, false);

	@Test
	void testName() throws Exception {
		final List<ScrapeRule> rules = Arrays.asList(newScrapeRule("java.lang:type=OperatingSystem:SystemCpuLoad", false), newScrapeRule(null, true));
		final Scraper scraper = new Scraper(ManagementFactory.getPlatformMBeanServer(), rules, null);

		final Set<Sample> actual = new HashSet<>();
		scraper.scrape((sample) -> {
			actual.add(sample);
		});

		assertEquals(1, actual.size());
	}

	@Test
	void testSlowScrape() throws Exception {
		final Scraper scraper = new Scraper(ManagementFactory.getPlatformMBeanServer(), Collections.emptyList(), DEFAULT_INCLUSION_RULE);

		final long start = System.currentTimeMillis();

		final Set<Sample> actual = new HashSet<>();
		scraper.scrape((sample) -> {
			actual.add(sample);
		}, 3, TimeUnit.SECONDS);

		assertTrue(3000L <= System.currentTimeMillis() - start);
		assertTrue(10 < actual.size());
	}
}
