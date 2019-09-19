package net.thisptr.java.prometheus.metrics.agent.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;

import net.thisptr.jackson.jq.internal.misc.Pair;
import net.thisptr.java.prometheus.metrics.agent.Sample;
import net.thisptr.java.prometheus.metrics.agent.misc.AttributeNamePattern;

public class Scraper<ScrapeRuleType extends ScrapeRule> {
	private static final Logger LOG = Logger.getLogger(Scraper.class.getName());

	private final List<ScrapeRuleType> rules;
	private final MBeanServer server;

	public Scraper(final MBeanServer server, final List<ScrapeRuleType> rules) {
		this.server = server;
		this.rules = rules;
	}

	private Pair<Boolean, ScrapeRuleType> findRuleEarly(final ObjectName name) {
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty())
				return Pair.of(true, rule); // found
			boolean nameMatches = false;
			for (final AttributeNamePattern pattern : rule.patterns()) {
				if (pattern.nameMatches(name)) {
					nameMatches = true;
					if (pattern.attribute == null)
						return Pair.of(true, rule); // found
				}
			}
			if (nameMatches)
				return Pair.of(false, null); // match depends on attribute, abort
		}
		return Pair.of(true, null); // default rule should be used
	}

	private ScrapeRuleType findRule(final ObjectName name, final String attribute) {
		for (final ScrapeRuleType rule : rules) {
			if (rule.patterns() == null || rule.patterns().isEmpty())
				return rule;
			for (final AttributeNamePattern pattern : rule.patterns())
				if (pattern.matches(name, attribute))
					return rule;
		}
		return null;
	}

	private class AttributeScrapeRequest {
		public final ObjectName name;
		public final MBeanInfo info;
		public final MBeanAttributeInfo attribute;
		public final ScrapeRuleType rule;

		public AttributeScrapeRequest(final ObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final ScrapeRuleType rule) {
			this.name = name;
			this.info = info;
			this.attribute = attribute;
			this.rule = rule;
		}
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output) throws InterruptedException {
		scrape(output, 0L, TimeUnit.MILLISECONDS);
	}

	public void scrape(final ScrapeOutput<ScrapeRuleType> output, final long duration, final TimeUnit unit) throws InterruptedException {
		final List<AttributeScrapeRequest> requests = new ArrayList<>();

		// We use server.queryNames() here, instead of server.queryMBeans(), to avoid costly server.getMBeanInfo() invoked internally.
		final Set<ObjectName> names;
		try {
			names = server.queryNames(null, null);
		} catch (final Throwable th) {
			LOG.log(Level.WARNING, "Failed to enumerate MBean names.", th);
			return;
		}

		for (final ObjectName name : names) {
			// Filter early by ObjectName because server.getMBeanInfo() is really slow.
			final Pair<Boolean, ScrapeRuleType> ruleByName = findRuleEarly(name);
			if (ruleByName._1) { // If we were able to determine rule solely by ObjectName
				if (ruleByName._2 != null && ruleByName._2.skip()) // and if the rule is to skip MBean
					continue;
			}

			final MBeanInfo info;
			try {
				info = server.getMBeanInfo(name);
			} catch (final Throwable th) {
				LOG.log(Level.FINER, "Failed to obtain MBeanInfo (name = " + name + ")", th);
				continue;
			}

			for (final MBeanAttributeInfo attribute : info.getAttributes()) {
				try {
					if (!attribute.isReadable())
						continue;

					final ScrapeRuleType rule;
					if (ruleByName._1) {
						rule = ruleByName._2;
					} else {
						rule = findRule(name, attribute.getName());
						if (rule != null && rule.skip())
							continue;
					}

					requests.add(new AttributeScrapeRequest(name, info, attribute, rule));
				} catch (final Throwable th) {
					LOG.log(Level.WARNING, "Failed to process MBean attribute (name = " + name + ", attribute = " + attribute.getName() + ").", th);
				}
			}
		}

		final long startNanos = System.nanoTime();
		final long durationNanos = unit.toNanos(duration);
		for (int i = 0; i < requests.size(); ++i) {
			final long waitUntilNanos = startNanos + (long) (((i + 1) / (double) requests.size()) * durationNanos);
			final long sleepNanos = waitUntilNanos - System.nanoTime();
			if (sleepNanos > 10_000_000) // sleep only when we are more than 10ms ahead, to avoid excessive context switches.
				sleepNanos(sleepNanos);
			final AttributeScrapeRequest request = requests.get(i);
			try {
				scrape(request.name, request.info, request.attribute, request.rule, output);
			} catch (final Throwable th) {
				LOG.log(Level.FINER, "Failed to scrape the attribute of the MBean instance (name = " + request.name + ", attribute = " + request.attribute.getName() + ")", th);
			}
		}
		// The previous loop can finish at most 10ms earlier than desired.
		final long waitUntilNanos = startNanos + durationNanos;
		final long sleepNanos = waitUntilNanos - System.nanoTime();
		if (sleepNanos > 0)
			sleepNanos(sleepNanos);
	}

	private static void sleepNanos(final long totalNanos) throws InterruptedException {
		final int nanos = (int) (totalNanos % 1000000L);
		final long millis = totalNanos / 1000000L;
		Thread.sleep(millis, nanos);
	}

	private void scrape(final ObjectName name, final MBeanInfo info, final MBeanAttributeInfo attribute, final ScrapeRuleType rule, final ScrapeOutput<ScrapeRuleType> output) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
		final long timestamp = System.currentTimeMillis();
		final Object value;
		try {
			value = server.getAttribute(name, attribute.getName());
		} catch (final RuntimeMBeanException e) {
			if (e.getCause() instanceof UnsupportedOperationException)
				return;
			throw e;
		}

		output.emit(new Sample<>(rule, timestamp, name, info, attribute, value));
	}
}
