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
import net.thisptr.jmx.exporter.agent.handler.ConditionScript;
import net.thisptr.jmx.exporter.agent.misc.AttributeNamePattern;

public class ScraperTest {

	private static class Rule implements ScrapeRule {
		private final boolean skip;
		private final AttributeNamePattern pattern;

		public Rule(final AttributeNamePattern pattern, final boolean skip) {
			this.pattern = pattern;
			this.skip = skip;
		}

		@Override
		public List<AttributeNamePattern> patterns() {
			if (pattern == null)
				return null;
			return Arrays.asList(pattern);
		}

		@Override
		public ConditionScript condition() {
			return null;
		}

		@Override
		public boolean skip() {
			return skip;
		}
	}

	private static final Rule DEFAULT_INCLUSION_RULE = new Rule(null, false);

	@Test
	void testName() throws Exception {
		final List<Rule> rules = Arrays.asList(new Rule(AttributeNamePattern.compile("java.lang:type=OperatingSystem:SystemCpuLoad"), false), new Rule(null, true));
		final Scraper<Rule> scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), rules, null);

		final Set<Sample<Rule>> actual = new HashSet<>();
		scraper.scrape((sample) -> {
			actual.add(sample);
		});

		assertEquals(1, actual.size());
	}

	@Test
	void testSlowScrape() throws Exception {
		final Scraper<Rule> scraper = new Scraper<>(ManagementFactory.getPlatformMBeanServer(), Collections.emptyList(), DEFAULT_INCLUSION_RULE);

		final long start = System.currentTimeMillis();

		final Set<Sample<Rule>> actual = new HashSet<>();
		scraper.scrape((sample) -> {
			actual.add(sample);
		}, 3, TimeUnit.SECONDS);

		assertTrue(3000L <= System.currentTimeMillis() - start);
		assertTrue(10 < actual.size());
	}
}
