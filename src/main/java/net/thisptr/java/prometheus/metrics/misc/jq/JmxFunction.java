package net.thisptr.java.prometheus.metrics.misc.jq;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.java.prometheus.metrics.agent.scraper.Scraper;

public class JmxFunction implements Function {

	private static JsonNode get(final MBeanServer server, final String name, final String attribute) throws InstanceNotFoundException, ReflectionException, MBeanException, MalformedObjectNameException, AttributeNotFoundException {
		AttributeNotFoundException th = null;
		for (final ObjectName on : server.queryNames(new ObjectName(name), null)) {
			try {
				final Object value = server.getAttribute(on, attribute);
				return Scraper.JMX_MAPPER.valueToTree(value);
			} catch (AttributeNotFoundException e) {
				th = e;
				continue;
			}
		}
		if (th != null)
			throw th;
		throw new InstanceNotFoundException(name);
	}

	@Override
	public List<JsonNode> apply(final Scope scope, final List<JsonQuery> args, final JsonNode in) throws JsonQueryException {
		final MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		final JsonQuery nameExpr = args.get(0);
		final JsonQuery attributeExpr = args.get(1);

		final List<JsonNode> out = new ArrayList<>();

		for (final JsonNode name : nameExpr.apply(scope, NullNode.getInstance())) {
			if (!name.isTextual())
				throw new JsonQueryException("objectname must be string");
			for (final JsonNode attribute : attributeExpr.apply(scope, NullNode.getInstance())) {
				if (!attribute.isTextual())
					throw new JsonQueryException("attribute must be string");

				try {
					out.add(get(server, name.asText(), attribute.asText()));
				} catch (final Exception e) {
					throw new JsonQueryException(e);
				}
			}
		}

		return out;
	}
}
