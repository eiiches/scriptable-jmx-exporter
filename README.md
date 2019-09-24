java-prometheus-metrics-agent
=============================

A javaagent for scraping and exposing MBeans to Prometheus

[![GitHub Actions](https://github.com/eiiches/java-prometheus-metrics-agent/workflows/test/badge.svg)](https://github.com/eiiches/java-prometheus-metrics-agent/actions)

Features
--------

- By default, this agent exposes all the MBeans available.
- Scraping behavior can be customized using jq scripts.

### Features missing

- TYPE and HELP annotations.


Installation
------------

#### Build from source

Clone this repository and run `mvn clean package`. A agent jar will be built and appear at `target/java-prometheus-metrics-agent-{version}.jar`.

#### Download from Maven Central

```sh
curl -O 'http://central.maven.org/maven2/net/thisptr/java-prometheus-metrics-agent/0.0.4/java-prometheus-metrics-agent-0.0.4.jar'
```

Usage
-----

Add `-javaagent` option to JVM arguments.

```sh
java -javaagent:<PATH_TO_AGENT_JAR> ...
```

Configurations can be passed as a javaagent argument.

```sh
# Pass YAML (or JSON) directly
java -javaagent:<PATH_TO_AGENT_JAR>=<CONFIG_YAML> ...

# Load configurations from PATH_TO_CONFIG_YAML
java -javaagent:<PATH_TO_AGENT_JAR>=@<PATH_TO_CONFIG_YAML> ...
```


Configuration
-------------

### Example

```yaml
# You can omit `server` object if you use default `bind_address`.
server:
    bind_address: '0.0.0.0:18090'
options:
    include_timestamp: true # This is the default value.
    minimum_response_time: 3000 # Generate /metrics response slowly in 3 seconds to avoid CPU spikes.
rules:
  - pattern:
      - 'com\\.sun\\.management:type=HotSpotDiagnostic:DiagnosticOptions'
      - 'java\\.lang:type=Threading:AllThreadIds'
    # Drop less useful attributes JVM exposes.
    skip: true
  - pattern:
      - '::.*MinuteRate'
      - '::MeanRate'
    # Some instrumentation libraries (such as Dropwizard Metrics) expose pre-calculated rate statistics.
    # Since Prometheus can calculate rates itself, just drop them.
    skip: true
  - pattern: 'java.lang:type=GarbageCollector:LastGcInfo'
    # NOTE: You probably don't need to do this; Just a demo.
    # For MBean attributes in `java.lang` domain, put the value of the `type` key property and
    # the attribute name in Prometheus metric names, separated by colons. Also, rename `memoryUsageAfterGc_key`
    # and `memoryUsageBeforeGc_key` labels auto-generated from TabularData to `heap`.
    transform: |
        default_transform_v1(["type"]; true; {memoryUsageAfterGc_key: "heap", memoryUsageBeforeGc_key: "heap"})
  - pattern: 'java.lang'
    # NOTE: You probably don't need to do this; Just a demo.
    # For MBean attributes in `java.lang` domain, put the value of the `type` key property and
    # the attribute name in Prometheus metric names, separated by colons.
    transform: |
        default_transform_v1(["type"]; true)
  - pattern: 'procfs'
    # Default transform is default_transform_v1, so this rule is effectively a no-op. Just for a demo purpose.
    transform: default_transform_v1
# Add os_version and jvm_version labels to all metrics.
labels: |
  {
    os_version: jmx("java.lang:type=OperatingSystem"; "Version"),
    jvm_version: jmx("java.lang:type=Runtime"; "VmVersion"),
    app_version: "1.0.1"
  }
```

This YAML is mapped to [Config](src/main/java/net/thisptr/java/prometheus/metrics/agent/config/Config.java) class using Jackson data-binding and validated by Hibernate validator.

See [wiki](https://github.com/eiiches/java-prometheus-metrics-agent/wiki) for more examples.

### Server Configuration

| Key | Default | Description |
|-|-|-|
| `server.bind_address` | `0.0.0.0:18090` | IP and port which this javaagent listens on. |

### Handler Options

| Key | Default | Description |
|-|-|-|
| `options.include_timestamp` | `true` | Specifies whether /metrics response should include a timestamp at which the metric is scraped. |
| `options.minimum_response_time` | `0` | A minimum time in milliseconds which every /metrics requests should take. This is used to avoid CPU spikes when there are thousands of metrics. When set, `options.include_timestamp` should not be disabled because the time at which a response completes differs from the time at which the metrics are scraped. |

These options can also be specified as /metrics parameters. E.g. `/metrics?minimum_response_time=1000`.

### Rule Configuration

Rules are searched in order and a first match is used for each attribute.

| Key | Default | Description |
|-|-|-|
| `rules[].pattern` | `null` | A pattern used to match MBean attributes this rule applies to. A rule with a `null` pattern applies to any attributes. See [Pattern Format](#pattern-format) for syntax details. |
| `rules[].skip` | `false` | If `true`, skip exposition of the attribute to Prometheus. |
| `rules[].transform` | `default_transform_v1` | A jq script to convert an MBean attribute to Prometheus metrics. See [Transform Script](#transform-script) for details. |

### Labels Configuration

| Key | Default | Description |
|-|-|-|
| `labels` | `{}` | A static object containing labels or a jq expression (string) to generate labels at runtime. The labels are added to every metrics. |

#### Pattern Format

TBD

#### Transform Script

TBD

##### Using generic `default_transform_v1` function

Writing a generic `transform` script from scratch is hard because MBean attributes contain variety of types including a nested ones such as CompositeData, TabularData, etc.
Thus, we provide the following `transform` implementations.

- default_transform_v1 ([prometheus.jq#L44](src/main/resources/prometheus.jq#44))

  Same as `default_transform_v1([]; false; {})`.

- default_transform_v1(List&lt;String&gt; name_keys, boolean attribute_as_metric_name) ([prometheus.jq#L30](src/main/resources/prometheus.jq#30))

  Same as `default_transform_v1(name_keys; attribute_as_metric_name; {})`

- default_transform_v1(List&lt;String&gt; name_keys, boolean attribute_as_metric_name, Map&lt;String, String&gt; label_remapping) ([prometheus.jq#L45](src/main/resources/prometheus.jq#45))

Given the following MBean attribute JSON,

```javascript
{
  "type": "javax.management.openmbean.CompositeData",
  "value": {
    "$type": "javax.management.openmbean.CompositeData",
    "committed": 99745792,
    "init": 0,
    "max": 134217728,
    "used": 95750352
  },
  "domain": "java.lang",
  "properties": {
    "type": "MemoryPool",
    "name": "Metaspace"
  },
  "attribute": "Usage"
}
```

1. `default_transform_v1` produces

   ```javascript
   {"name":"java.lang","labels":{"type":"MemoryPool","name":"Metaspace","attribute":"Usage_committed"},"value":99745792}
   {"name":"java.lang","labels":{"type":"MemoryPool","name":"Metaspace","attribute":"Usage_init"},"value":0}
   {"name":"java.lang","labels":{"type":"MemoryPool","name":"Metaspace","attribute":"Usage_max"},"value":134217728}
   {"name":"java.lang","labels":{"type":"MemoryPool","name":"Metaspace","attribute":"Usage_used"},"value":95750352}
   ```
   
   which corresponds to the following lines in [Prometheus exposition format](https://github.com/prometheus/docs/blob/master/content/docs/instrumenting/exposition_formats.md).
   
   ```text
   java_lang{type="MemoryPool", name="Metaspace", attribute="Usage_committed", } 99745792.0
   java_lang{type="MemoryPool", name="Metaspace", attribute="Usage_init", } 0.0
   java_lang{type="MemoryPool", name="Metaspace", attribute="Usage_max", } 134217728.0
   java_lang{type="MemoryPool", name="Metaspace", attribute="Usage_used", } 95750352.0
   ```
   
   Note that illegal characters in metric name and label names are automatically replaced by `_`. See [Data Model](https://prometheus.io/docs/concepts/data_model/) in the Prometheus documentation for metrics naming.

2. `default_transform_v1(["type"]; false)`
   
   ```text
   java_lang:MemoryPool{name="Metaspace", attribute="Usage_committed", } 99745792.0
   java_lang:MemoryPool{name="Metaspace", attribute="Usage_init", } 0.0
   java_lang:MemoryPool{name="Metaspace", attribute="Usage_max", } 134217728.0
   java_lang:MemoryPool{name="Metaspace", attribute="Usage_used", } 95750352.0
   ```

3. `default_transform_v1(["type"]; true)`
   
   ```text
   java_lang:MemoryPool:Usage_committed{name="Metaspace", } 99745792.0
   java_lang:MemoryPool:Usage_init{name="Metaspace", } 0.0
   java_lang:MemoryPool:Usage_max{name="Metaspace", } 134217728.0
   java_lang:MemoryPool:Usage_used{name="Metaspace", } 95750352.0
   ```

4. `default_transform_v1(["type"]; true, {"name": "heapname"})`
   
   ```text
   java_lang:MemoryPool:Usage_committed{heapname="Metaspace", } 99745792.0
   java_lang:MemoryPool:Usage_init{heapname="Metaspace", } 0.0
   java_lang:MemoryPool:Usage_max{heapname="Metaspace", } 134217728.0
   java_lang:MemoryPool:Usage_used{heapname="Metaspace", } 95750352.0
   ```

### Debugging

Sometimes it's hard to debug complex `transform` scripts. Here's some tips and tricks to debug them.

#### Fetching all MBeans as JSON

You can navigate to `http://<HOST>:<PORT>/mbeans` on your browser and it will print all the MBeans in JSON.
These JSONs can be fed into your `transform` script using jq (see below).

#### Using jq to test `transform` script

Pre-defined jq functions can be found at [src/main/resources/prometheus.jq](src/main/resources/prometheus.jq). You can include it in jq to test your `transform` script. E.g.

```sh
jq -c 'include "src/main/resources/prometheus"; default_transform_v1'
```

#### Changing a log level to FINEST

This agent uses JUL framework for logging. Errors caused by user configurations are logged at &gt;= INFO level. There are still some errors
logged at &lt; INFO (such as FINEST level). Consider setting the log level to FINEST, especially if you are silently missing some attributes.

To change the log level, create your `logging.properties` and set its path to `java.util.logging.config.file` system property.

```console
$ cat logging.properties
handlers = java.util.logging.ConsoleHandler
.level = INFO
java.util.logging.ConsoleHandler.level = FINEST
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format = %1$tFT%1$tT.%1$tL %4$-5.5s %3$-80.80s : %5$s%6$s%n
net.thisptr.java.prometheus.metrics.agent.level = FINEST
net.thisptr.java.prometheus.metrics.agent.shade.level = INFO

$ java -Djava.util.logging.config.file=logging.properties ...
```

FAQ
---

### Why yet another exporter when there's an official one?

- The order of key properties of an MBean's `ObjectName` is not significant. In fact, some `ObjectName`s are constructed using a `Hashtable` and doesn't have a consistent order of the key properties.

  - Writing a regular expression against a string representation of an `ObjectName` is tricky because the key properties part doesn't have a predictable ordering.

  - The default metric name which includes a value of the first key property doesn't work well because being "the first" doesn't mean anything. This prevents me from using the default configuration and I have to write many regular expressions, which is difficult.

- "Metric traceability" is crucial for me.

  - I don't need the metrics such as `jvm_memory_bytes_used` which is just a renamed version of `java.lang:type=Memory:HeapMemoryUsage.used`. They are exposed automatically and can't be turned off. Renaming a metric obfuscates where the metric comes from and what the metric actually means.

  - One generic rule that one-to-one maps all MBeans available to Prometheus metrics would be ideal.


References
----------

 - [Java Management Extensions (JMX) - Best Practices](http://www.oracle.com/technetwork/articles/java/best-practices-jsp-136021.html)


License
-------

The MIT License.
