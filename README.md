*If you are reading this on `develop` branch, some of the features may not be present yet on the released versions.*

Scriptable JMX Exporter
=======================

Java agent to scrape and expose MBeans to Prometheus. Formerly, java-prometheus-metrics-agent.

[![GitHub Actions](https://github.com/eiiches/scriptable-jmx-exporter/workflows/test/badge.svg)](https://github.com/eiiches/scriptable-jmx-exporter/actions)

Features
--------

- Easy configuration. Defining a few generic rules should be enough for most users.
  - MBeans attributes can be pattern matched by straightforward ObjectName-aware Regex. E.g.
    - `java.*`
    - `java.lang:type=OperatingSystem|Threading`
    - `java.lang:type=GarbageCollector:LastGcInfo` matches regardless of `name=` key property.
    - `foo:type=A,name=B` is equivalent to `foo:name=B,type=A`.
  - All non-textual attributes from all MBeans are exposed by default.
- Scripting in Java, which enables powerful customization. E.g.
  - Converting a textual attribute to a numeric value (normal metric) or metric label with value 1 (info-style metric).
  - Decomposing complex MBean name into metric labels.
- Performance. Goal is to enable a large number of metrics (~50k) at shorter intervals (>1s) without impacting workloads.
  - See [benchmark](#benchmark) (TBD).

### Why another exporter? There's a bunch of exporters and there's even the official one.

I needed something that can scrape many MBeans with a small number of rules. Writing a regex for each set of *key properties* (key=value,... part of MBean name) was impossibly hard, especially when
*key properties* doesn't always have a consistent order depending on how it is constructed (because it's just a hash table).


Installation
------------

#### Requirements

* Java 8 or newer

#### Download from the Maven Central (Recommended)

[https://repo1.maven.org/maven2/net/thisptr/java-prometheus-metrics-agent/0.0.5/java-prometheus-metrics-agent-0.0.5.jar](https://repo1.maven.org/maven2/net/thisptr/java-prometheus-metrics-agent/0.0.5/java-prometheus-metrics-agent-0.0.5.jar)

#### Building from source

```sh
git clone https://github.com/eiiches/scriptable-jmx-exporter
cd scriptable-jmx-exporter
mvn clean package
```

An agent jar will be built and available at `target/scriptable-jmx-exporter-{version}.jar`.

Usage
-----

Add `-javaagent` option to JVM arguments. See Configuration section for details.

```sh
# Without javaagent arguments, configurations from src/main/resources/scriptable-jmx-exporter.yaml is used.
java -javaagent:<PATH_TO_AGENT_JAR> ...
```

Configurations can be passed as a javaagent argument.

```sh
# Set configurations in YAML (or JSON) directly on command line
java -javaagent:<PATH_TO_AGENT_JAR>=<CONFIG_YAML> ...

# e.g.
# java -javaagent:scriptable-jmx-exporter-0.0.6.jar='{"rules":[{"pattern":["com.sun.management:type=HotSpotDiagnostic:DiagnosticOptions","java.lang:type=Threading:AllThreadIds","jdk.management.jfr"],"skip":true},{"transform":"!java V1.transform(in, out, \"type\")"}]}' ...

# ---
# Load configurations from PATH_TO_CONFIG_YAML file
java -javaagent:<PATH_TO_AGENT_JAR>=@<PATH_TO_CONFIG_YAML> ...

# e.g.
# java -javaagent:scriptable-jmx-exporter-0.0.6.jar=@/etc/foo.yaml ...
# java -javaagent:scriptable-jmx-exporter-0.0.6.jar=@foo.yaml ...
# java -javaagent:scriptable-jmx-exporter-0.0.6.jar=@classpath:foo.yaml ...
```

Configuration
-------------

### Automatic Reloading

Configurations are automatically reloaded whenever the file (`<PATH_TO_CONFIG_YAML>` in the description above) is modified. This behavior cannot be turned off (at least for now).

So, whenever you need to write a new configuration, it's easier to start with a simple configuration (e.g. [scriptable-jmx-exporter.yaml](https://github.com/eiiches/scriptable-jmx-exporter/blob/develop/src/main/resources/scriptable-jmx-exporter.yaml) which is the default configuration picked when no configuration is provided on command line) and incrementally edit the configuration file while actually running your software.

If the agent fails to load a new configuration, most likely due to configuration error, the agent will continue to use the previous configuration. On the contrary, application startup will fail if the configuration has any errors.
It's generally considered safe (in a sense that it will not interrupt running workloads) to reconfigure agents on a production cluster while they are running. 

### Full Example

```yaml
# You can omit `server` and `options` if you are happy with the default values
server:
  bind_address: '0.0.0.0:18090' # default
options:
  include_timestamp: true # Include scraping timestamp for each metrics (default).
  include_type: true # Enable TYPE comments (default).
  include_help: true # Enable HELP comments (default).
rules:
- pattern:
  # Drop less useful attributes JVM exposes.
  - 'com\\.sun\\.management:type=HotSpotDiagnostic:DiagnosticOptions'
  - 'java\\.lang:type=Threading:AllThreadIds'
  - 'jdk\\.management\\.jfr'
  # Some instrumentation libraries (such as Dropwizard Metrics) expose pre-calculated rate statistics.
  # Since Prometheus can calculate these values by itself, we don't need them. Skip.
  - '::.*MinuteRate'
  - '::MeanRate'
  skip: true
# Rule for known MBeans.
- pattern: 'java\\.lang|java\\.nio|jboss\\.threads|net\\.thisptr\\.jmx\\.exporter\\.agent.*'
  transform: |
    !java
    V1.transform(in, out, "type", V1.snakeCase());
# Default rule to cover the rest.
- transform: |
    !java
    V1.transform(in, out, "type", V1.snakeCase());
```

This YAML is mapped to [Config](src/main/java/net/thisptr/jmx/exporter/agent/config/Config.java) class using Jackson data-binding and validated by Hibernate validator.

See [wiki](https://github.com/eiiches/scriptable-jmx-exporter/wiki) for real-world examples.

### Server Configuration

| Key | Default | Description |
|-|-|-|
| `server.bind_address` | `0.0.0.0:18090` | IP and port to listen and servce metrics on. |

### Handler Options

| Key | Default | Description |
|-|-|-|
| `options.include_timestamp` | `true` | Specifies whether /metrics response should include a timestamp at which the metric is scraped. |
| `options.include_help` | `true` | Enables HELP comment. |
| `options.include_type` | `true` | Enables TYPE comment. |
| `options.minimum_response_time` | `0` | A minimum time in milliseconds which every /metrics requests should take. This is used to avoid CPU spikes when there are thousands of metrics. When set, `options.include_timestamp` should not be disabled because the time at which a response completes differs from the time at which the metrics are scraped. |

These options can also be specified as /metrics parameters. E.g. `/metrics?minimum_response_time=1000`.

### Rule Configuration

Rules are searched in order and a first match is used for each attribute.

| Key | Default | Description |
|-|-|-|
| `rules[].pattern` | `null` | A pattern used to match MBean attributes this rule applies to. A rule with a `null` pattern applies to any attributes. See [Pattern Format](#pattern-format) for syntax details. |
| `rules[].skip` | `false` | If `true`, skip exposition of the attribute to Prometheus. |
| `rules[].transform` | `default_transform_v1` | A script to convert an MBean attribute to Prometheus metrics. See [Scripting](#scripting) for details. |

### Labels Configuration (Deprecated)

| Key | Default | Description |
|-|-|-|
| `labels` | `{}` | A static object containing labels or a jq expression (string) to generate labels at runtime. The labels are added to every metrics. |

DEPRECATION: This will be removed in future versions. The purpose of this feature was to add software versions, etc. to all the metrics, but that wasn't a right solution. Use info-style metrics for that.

### Pattern Format

TBD

Scripting
---------

While we support both Java and jq as a scripting language, Java should be the preferred choice as it (i) can detect most of the type errors earlier, (ii) has rich standard libraries, (iii) is faster.

### Java

Java scripting is powered by [Janino](https://janino-compiler.github.io/janino/), which is a super-small, super-fast Java compiler. To write scripts in Java, the script must start with `!java` directive. E.g.

```yaml
- transform: |
    !java
    V1.transform(in, out, "type", V1.snakeCase());
```

Two variables, `in` (type: [AttributeValue](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/AttributeValue.java)) and `out` (type: [MetricValueOutput](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/MetricValueOutput.java)) is provided.

What the script has to do is to, transform `in`, which is a value (and metadata) of MBean attribute, into a [MetricValue](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/MetricValue.java) object and call `out.emit(...)` with the metric object.

#### Helper Functions (V1)

Implementing the transformation from scratch is not easy. So, we provide [V1](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/v1/V1.java), a set of generic helper functions.
In most cases, doing one of the following is sufficient to achieve the desired output.
 * Change arguments to `V1.transform(...)`
 * Modify `in` before calling `V1.transform(...)`
 * Wrap `out` by anonymous inner class to modify `V1.transform(...)` output

#### Examples

##### Example: Exposing all attributes of all MBaens

For most of the applications, this rule covers most of the MBean attributes.

```yaml
- transform: |
    !java
    V1.transform(in, out, "type", V1.snakeCase());
```

```
java_lang_garbage_collector_last_gc_info_memory_usage_after_gc_value_committed{name="G1 Young Generation",key="CodeHeap 'profiled nmethods'",} 7995392.0 1595172454731
java_lang_memory_heap_memory_usage_committed 1.073741824E9 1595172454742
java_nio_buffer_pool_count{name="direct",} 14.0 1595172454721
...
```

##### Example: Exposing versions as info-style metrics

```yaml
- pattern:
  - java.lang:type=Runtime:VmVersion
  - java.lang:type=OperatingSystem:Version
  transform: |
    !java
    import java.util.HashMap;
    MetricValue m = new MetricValue();
    m.name = in.domain + ":" + in.keyProperties.get("type") + ":" + in.attributeName + "_info";
    m.labels = new HashMap<>();
    m.labels.put("version", (String) in.value);
    m.value = 1.0;
    m.timestamp = in.timestamp;
    V1.snakeCase().apply(m);
    out.emit(m);
```

```
java_lang_runtime_vm_version_info{version="14.0.1+7",} 1.0 1595167009825
java_lang_operating_system_version_info{version="5.7.4-arch1-1",} 1.0 1595167009828
```

Reference: [Exposing the software version to Prometheus](https://www.robustperception.io/exposing-the-software-version-to-prometheus)

##### Example: Exposing thread counts by thread state

```yaml
- pattern: java.lang:type=Threading:AllThreadIds
  transform: |
    !java
    import java.lang.management.ManagementFactory;
    import java.lang.management.ThreadInfo;
    import java.lang.management.ThreadMXBean;
    import java.util.HashMap;

    Thread.State[] states = Thread.State.values();
    int[] counts = new int[states.length];

    long timestamp = System.currentTimeMillis();
    ThreadInfo[] threads = ManagementFactory.getThreadMXBean().getThreadInfo((long[]) in.value, 0);
    for (ThreadInfo thread : threads) {
      if (thread == null)
        continue;
      ++counts[thread.getThreadState().ordinal()];
    }

    for (int i = 0; i < states.length; ++i) {
      MetricValue m = new MetricValue();
      m.name = "java_lang_threading_state_count";
      m.labels = new HashMap<>();
      m.labels.put("state", states[i].name());
      m.value = counts[i];
      m.timestamp = timestamp;
      m.type = "gauge";
      m.help = "The number of threads in the state. This value is calculated from ThreadMXBean#getThreadInfo(java.lang:type=Threading:AllThreadIds).";
      out.emit(m);
    }
```

```
# HELP java_lang_threading_state_count The number of threads in the state. This value is calculated from ThreadMXBean#getThreadInfo(java.lang:type=Threading:AllThreadIds).
# TYPE java_lang_threading_state_count gauge
java_lang_threading_state_count{state="NEW",} 0.0 1595170784228
java_lang_threading_state_count{state="RUNNABLE",} 11.0 1595170784228
java_lang_threading_state_count{state="BLOCKED",} 0.0 1595170784228
java_lang_threading_state_count{state="WAITING",} 1.0 1595170784228
java_lang_threading_state_count{state="TIMED_WAITING",} 3.0 1595170784228
java_lang_threading_state_count{state="TERMINATED",} 0.0 1595170784228
```

##### Example: Adding computed metrics

This is just for demonstration purpose and highly discouraged in practice unless absolutely necessary because these kind of metrics computation makes it hard to trace a metric back to its source and how the value is generated. In most cases, we don't have to do this at all, because Prometheus can perform complex query including arithmetic.

```yaml
- pattern: 'java\\.lang:type=OperatingSystem:OpenFileDescriptorCount'
  transform: |
    !java
    import java.lang.management.ManagementFactory; // imports must come first
    import javax.management.ObjectName;

    V1.transform(in, out, "type", V1.snakeCase(), V1.gauge()); // emit raw metric

    // modify name and values and emit computed metric
    long max = (Long) ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "MaxFileDescriptorCount");
    in.value = max - (Long) in.value;
    in.attributeName = "AvailableFileDescriptorCount";
    in.attributeDescription = "The number of file descriptors available to be opened in this JVM, which is calculated as java.lang:type=OperatingSystem:MaxFileDescriptorCount - java.lang:type=OperatingSystem:OpenFileDescriptorCount.";
    V1.transform(in, out, "type", V1.snakeCase(), V1.gauge());
```

```
# HELP java_lang_operating_system_available_file_descriptor_count The number of file descriptors available to be opened in this JVM, which is calculated as java.lang:type=OperatingSystem:MaxFileDescriptorCount - java.lang:type=OperatingSystem:OpenFileDescriptorCount.
# TYPE java_lang_operating_system_available_file_descriptor_count gauge
java_lang_operating_system_available_file_descriptor_count 1048546.0 1595164426599
# HELP java_lang_operating_system_open_file_descriptor_count OpenFileDescriptorCount
# TYPE java_lang_operating_system_open_file_descriptor_count gauge
java_lang_operating_system_open_file_descriptor_count 30.0 1595164426599
```



### jq (Deprecated)

DEPRECATION: We are phasing out jq scripting feature. Please migrate to Java.

Roughly speaking, the following two behaves the same, except for TabularData where label naming is changed
(e.g. For `java.lang:type=GarbageCollector:LastGcInfo.memoryUsageAfterGc` which is a TabularData nested inside CompositeData,
one of the metric label, `memoryUsageAfterGc_key` is changed to just `key` in `V1.transform()` in Java).

```yaml
- transform: |
    default_transform_v1(["foo", "bar"]; true)
```

```yaml
- transform: |
    !java
    V1.transform(in, out, "foo", "bar");
```

Detailed explanation for jq scripting is removed. Please refer to [older README.md](https://github.com/eiiches/scriptable-jmx-exporter/tree/java-prometheus-metrics-agent-0.0.5).

### Debugging

Sometimes it's hard to debug complex `transform` scripts. Here's some tips and tricks to debug them.

#### Changing a log level to FINEST

This agent uses JUL framework for logging. Errors caused by user configurations are logged at &gt;= INFO level. Other errors are
logged at &lt; INFO level. If you are encountering issues, consider setting log levels to FINEST to see detailed logs.

To change a log level, create `logging.properties` and set `java.util.logging.config.file` system property to point to the file.

```console
$ cat logging.properties
handlers = java.util.logging.ConsoleHandler
.level = INFO
java.util.logging.ConsoleHandler.level = FINEST
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format = %1$tFT%1$tT.%1$tL %4$-5.5s %3$-80.80s : %5$s%6$s%n
net.thisptr.jmx.exporter.agent.level = FINEST
net.thisptr.jmx.exporter.agent.shade.level = INFO

$ java -Djava.util.logging.config.file=logging.properties ...
```

If you are using jul-to-slf4j or log4j-jul to redirect JUL logs to another backend (such as log4j2, logback, ...), this may not work. Please consult the relevant documentations for your logging framework.

#### Using log() function

If you are writing transform scripts in Java, you can use `log(fmt, ...)` or `log(obj)` method. The logs will be recorded at INFO level.

```sh
- pattern: 'java.lang'
  transform: |
    !java
    log(in);
    log("hi");
    log("test: %s", in.value); # printf style; see javadoc for String.format()
    V1.transform(in, out, "type", V1.snakeCase());
```

Alternatively, you can also use `System.out.printf(...)` or `System.err.printf(...)` as in any other programs.


Benchmark
---------

TBD


References
----------

- [Java Management Extensions (JMX) - Best Practices](http://www.oracle.com/technetwork/articles/java/best-practices-jsp-136021.html)


License
-------

The MIT License.
