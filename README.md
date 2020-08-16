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
  - See our [benchmark](#benchmark).


#### Why another exporter? There's a bunch of exporters and there's even the official one.

I needed something that can scrape many MBeans with a small number of rules. Writing a regex for each set of *key properties* (key=value,... part of MBean name) was impossibly hard, especially when
*key properties* doesn't always have a consistent order depending on how it is constructed (because it's just a hash table).


#### Requirements

* Java 8 or newer


Quick Start
------------

*If you don't want to run the agent now, download [scriptable-jmx-exporter-1.0.0-alpha2.jar](https://repo1.maven.org/maven2/net/thisptr/scriptable-jmx-exporter/1.0.0-alpha2/scriptable-jmx-exporter-1.0.0-alpha2.jar) and skip to [Usage](#usage).*

You can quickly try out this exporter by copy-and-pasting the following snippet to your shell (or by manually running one by one).
This will download the agent jar and a default configuration file, and then start the exporter using `-javaagent` option.

```sh
# Download the agent jar and a default configuration file.
curl -LO https://repo1.maven.org/maven2/net/thisptr/scriptable-jmx-exporter/1.0.0-alpha2/scriptable-jmx-exporter-1.0.0-alpha2.jar
curl -LO https://raw.githubusercontent.com/eiiches/scriptable-jmx-exporter/v1.0.0-alpha2/src/main/resources/scriptable-jmx-exporter.yaml

# Finally, run JVM with the exporter enabled.
java -javaagent:scriptable-jmx-exporter-1.0.0-alpha2.jar=@scriptable-jmx-exporter.yaml net.thisptr.jmx.exporter.tools.Pause
```

Now, open [http://localhost:9639/metrics](http://localhost:9639/metrics) in your browser to see the exposed metrics.

The next step is to replace the Pause program with your favorite application that you want to monitor.
Continue reading or alternatively you can check out real-world [examples](examples) to learn how to customize the exporter.


Usage
-----

Add `-javaagent` option to JVM arguments.

```sh
# This starts an exporter without an explicit configuration file.
# The default configuration from src/main/resources/scriptable-jmx-exporter.yaml is used.
java -javaagent:<PATH_TO_AGENT_JAR> ...
```

Configurations can be passed as a javaagent argument. See Configuration section for details.

```sh
# Set configurations in JSON directly on command line (YAML is not supported here)
java -javaagent:<PATH_TO_AGENT_JAR>=<CONFIG_JSON> ...

# e.g.
# java -javaagent:scriptable-jmx-exporter-1.0.0-alpha2.jar='{"rules":[{"pattern":["com.sun.management:type=HotSpotDiagnostic:DiagnosticOptions","java.lang:type=Threading:AllThreadIds","jdk.management.jfr"],"skip":true},{"transform":"!java V1.transform(in, out, \"type\")"}]}' ...

# ---
# Load configurations from PATH_TO_CONFIG_YAML file
java -javaagent:<PATH_TO_AGENT_JAR>=@<PATH_TO_CONFIG_YAML> ...

# e.g.
# java -javaagent:scriptable-jmx-exporter-1.0.0-alpha2.jar=@/etc/foo.yaml ...
# java -javaagent:scriptable-jmx-exporter-1.0.0-alpha2.jar=@foo.yaml ...
# java -javaagent:scriptable-jmx-exporter-1.0.0-alpha2.jar=@classpath:foo.yaml ...
```

If multiple comma-separated configurations are specified, former configurations are overriden by (or merged with) the latter ones.

```sh
java -javaagent:<PATH_TO_AGENT_JAR>=@<PATH_TO_CONFIG_YAML>,<CONFIG_JSON> ...

# e.g.
# java -javaagent:scriptable-jmx-exporter-1.0.0-alpha2.jar=@/etc/foo.yaml,'{"server":{"bind_address":":19639"}}' ...
```

Configuration
-------------

*This section requires a basic grasp of data models used in Java Management Extensions (JMX).
If you are new to this area and don't understand what ObjectName or MBean is, I strongly recommend you to read [Java Management Extensions (JMX) Best Practices](https://www.oracle.com/java/technologies/javase/management-extensions-best-practices.html) first.*

### Automatic Reloading

Configurations are automatically reloaded whenever the file (`<PATH_TO_CONFIG_YAML>` in the description above) is modified. This behavior cannot be turned off (at least for now).

So, whenever you need to write a new configuration, it's easier to start with a simple configuration (e.g. [scriptable-jmx-exporter.yaml](https://github.com/eiiches/scriptable-jmx-exporter/blob/develop/src/main/resources/scriptable-jmx-exporter.yaml) which is the default configuration picked when no configuration is provided on command line) and incrementally edit the configuration file while actually running your software.

If the exporter fails to load a new configuration, most likely due to configuration error, the exporter will continue to use the previous configuration. On the contrary, application startup will fail if the configuration has any errors.
It's generally considered safe (in a sense that it will not interrupt running workloads) to reconfigure the exporter on a production cluster while they are running.

### Example

```yaml
# You can omit `server` and `options` if you are happy with the default values
server:
  bind_address: '0.0.0.0:9639' # default
options:
  include_timestamp: true # Include scraping timestamp for each metrics (default).
  include_type: true # Enable TYPE comments (default).
  include_help: true # Enable HELP comments (default).
declarations: |
  public static void foo() {
    log("foo");
  }
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
    V1.transform(in, out, "type");
# Default rule to cover the rest.
- transform: |
    V1.transform(in, out, "type");
```

This YAML is mapped to [Config](src/main/java/net/thisptr/jmx/exporter/agent/config/Config.java) class using Jackson data-binding and validated by Hibernate validator.

See [examples](examples) directory for real-world examples.

### Server Configuration

| Key | Default | Description |
|-|-|-|
| `server.bind_address` | `0.0.0.0:9639` | IP and port to listen and servce metrics on. |

### Handler Options

| Key | Default | Description |
|-|-|-|
| `options.include_timestamp` | `true` | Specifies whether /metrics response should include a timestamp at which the metric is scraped. |
| `options.include_help` | `true` | Enables HELP comment. |
| `options.include_type` | `true` | Enables TYPE comment. |
| `options.minimum_response_time` | `0` | A minimum time in milliseconds which every /metrics requests should take. This is used to avoid CPU spikes when there are thousands of metrics. When set, `options.include_timestamp` should not be disabled because the time at which a response completes differs from the time at which the metrics are scraped. |

These options can be overridden by URL parameters. E.g. `/metrics?minimum_response_time=1000`.

### Declarations

You can define static classes and methods for use in transform scripts, condition expressions, etc. They will be automatically imported and available so you don't have to manually write `import` statements.
Make sure to add `public static` in the declarations; otherwise, the classes and methods won't be accessible.

```yaml
declarations: |
  import java.util.Map;

  public static void foo() {
    log("foo");
  }

  public static class Foo {
    // ...
  }
```

### Rule Configuration

Rules are searched in order and a first match is used for each attribute.

| Key | Default | Description |
|-|-|-|
| `rules[].pattern` | `null` | A pattern used to match MBean attributes this rule applies to. A rule with a `null` pattern applies to any attributes. See [Pattern Matching](#pattern-matching) for syntax details. |
| `rules[].condition` | `true` | If an expression is set, this rule is used only when the expression evaluates to true. This is useful if you want to match an MBean attribute other than by its name, such as by its class name, etc. See [Condition Expression](#condition-expression) for details. |
| `rules[].skip` | `false` | If `true`, skip exposition of the attribute to Prometheus. |
| `rules[].transform` | `V1.transform(in, out, "type")` | A script to convert an MBean attribute to Prometheus metrics. See [Scripting](#scripting) for details. |

#### Pattern Matching

Pattern matches are done against *unquoted* key properties. For example, `.*:name=foo` matches an ObjectName `domain:name=\"foo\"`.

TBD


#### Condition Expression

Condition expression, if set, further narrows down MBean attributes that the rule applies to, in addition to `pattern`.
If the condition evaluates to `false`, the MBean attribute will be handled by one of the subsequent rules (or the default rule if there's none).

The following variables are accessible from a condition expression.

| Variable Name | Type | Description |
|-|-|-|
| `mbeanInfo` | [javax.management.MBeanInfo](https://docs.oracle.com/en/java/javase/14/docs/api/java.management/javax/management/MBeanInfo.html) | MBean information |
| `attributeInfo` | [javax.management.MBeanAttributeInfo](https://docs.oracle.com/en/java/javase/14/docs/api/java.management/javax/management/MBeanAttributeInfo.html) | MBean attribute information |

##### Examples

* `mbeanInfo.getClassName().endsWith("JmxReporter$Timer")`

Scripting
---------

In this section, we mainly talk about transform scripts for use in `rules[].transform`.

Scripts can explicitly specify which scripting engine to use, by starting a script with `!<NAME>` directive. Currently, `!java` is the default (and only) engine and hence can be omitted.
There used to be `!jq` engine, but removed.

### Java

Java scripting is powered by [Janino](https://janino-compiler.github.io/janino/), which is a super-small, super-fast Java compiler.

```yaml
- transform: |
    !java
    V1.transform(in, out, "type");
```

Two variables, `in` (type: [AttributeValue](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/AttributeValue.java)) and `out` (type: [MetricValueOutput](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/MetricValueOutput.java)) is provided.

What the script has to do is to, transform `in`, which is a value (and metadata) of MBean attribute, into a [MetricValue](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/MetricValue.java) object and call `out.emit(...)` with the metric object.

#### Helper Functions (V1)

Implementing the transformation from scratch is not easy. So, we provide [V1](src/main/java/net/thisptr/jmx/exporter/agent/handler/janino/api/v1/V1.java), a set of generic helper functions.
In most cases, doing one of the following is sufficient to achieve the desired output.
 * Change arguments to `V1.transform(...)`
 * Modify `in` before calling `V1.transform(...)`
 * Wrap `out` by anonymous inner class to modify `V1.transform(...)` output

##### Case-style Conversion

*NOTE: We DO NOT recommend any case-style conversions.
While [Cc]amelCase with `_` in-between looks somewhat unpleasant, it conveys more information from the original ObjectName,
probably making it easier to track a Prometheus metric back to the corresponding MBean attribute later when debugging, etc.*

You can covert case-styles of metric name by using `V1.snakeCase()` or `V1.lowerCase()`.

###### Example: `java.lang:type=ClassLoading:LoadedClassCount`

| Transform Script | Prometheus Metric Example |
|-|-|
| `V1.transform(in, out, "type")` | `java_lang_ClassLoading_LoadedClassCount` |
| `V1.transform(in, out, "type", V1.snakeCase())` | `java_lang_class_loading_loaded_class_count` |
| `V1.transform(in, out, "type", V1.lowerCase())` | `java_lang_classloading_loadedclasscount` |

#### Examples

##### Example: Exposing all attributes of all MBaens

For most of the applications, this rule covers most of the MBean attributes.

```yaml
- transform: |
    !java
    V1.transform(in, out, "type");
```

```
java_nio_BufferPool_Count{name="direct",} 8 1596881052752
java_lang_GarbageCollector_LastGcInfo_memoryUsageAfterGc_value_committed{name="G1 Young Generation",key="CodeHeap 'profiled nmethods'",} 2752512 1596881052753
java_lang_Memory_HeapMemoryUsage_committed 1061158912 1596881052757
...
```

##### Example: Exposing versions as info-style metrics (Advanced)

```yaml
- pattern:
  - java.lang:type=Runtime:VmVersion
  - java.lang:type=OperatingSystem:Version
  transform: |
    !java
    import java.util.HashMap;
    MetricValue m = new MetricValue();
    m.name = in.domain + "_" + in.keyProperties.get("type") + "_" + in.attributeName + "_info";
    m.labels = new HashMap<>();
    m.labels.put("version", (String) in.value);
    m.value = 1.0;
    m.timestamp = in.timestamp;
    out.emit(m);
```

```
java_lang_Runtime_VmVersion_info{version="14.0.1+7",} 1.0 1595167009825
java_lang_OperatingSystem_Version_info{version="5.7.4-arch1-1",} 1.0 1595167009828
```

Reference: [Exposing the software version to Prometheus](https://www.robustperception.io/exposing-the-software-version-to-prometheus)

##### Example: Exposing thread counts by thread state (Advanced)

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

##### Example: Adding computed metrics (Advanced)

This is just for demonstration purpose and highly discouraged in practice unless absolutely necessary because these kind of metrics computation makes it hard to trace a metric back to its source and how the value is generated. In most cases, we don't have to do this at all, because Prometheus can perform complex query including arithmetic.

```yaml
- pattern: 'java\\.lang:type=OperatingSystem:OpenFileDescriptorCount'
  transform: |
    !java
    import java.lang.management.ManagementFactory; // imports must come first
    import javax.management.ObjectName;

    V1.transform(in, out, "type", V1.gauge()); // emit raw metric

    // modify name and values and emit computed metric
    long max = (Long) ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "MaxFileDescriptorCount");
    in.value = max - (Long) in.value;
    in.attributeName = "AvailableFileDescriptorCount";
    in.attributeDescription = "The number of file descriptors available to be opened in this JVM, which is calculated as java.lang:type=OperatingSystem:MaxFileDescriptorCount - java.lang:type=OperatingSystem:OpenFileDescriptorCount.";
    V1.transform(in, out, "type", V1.gauge());
```

```
# HELP java_lang_OperatingSystem_OpenFileDescriptorCount OpenFileDescriptorCount
# TYPE java_lang_OperatingSystem_OpenFileDescriptorCount gauge
java_lang_OperatingSystem_OpenFileDescriptorCount 29 1596880934872
# HELP java_lang_OperatingSystem_AvailableFileDescriptorCount The number of file descriptors available to be opened in this JVM, which is calculated as java.lang:type=OperatingSystem:MaxFileDescriptorCount - java.lang:type=OperatingSystem:OpenFileDescriptorCount.
# TYPE java_lang_OperatingSystem_AvailableFileDescriptorCount gauge
java_lang_OperatingSystem_AvailableFileDescriptorCount 1048547 1596880934872
```

#### Getting ready for upcoming OpenMetrics support

While this exporter does not support OpenMetrics yet, you can prepare for the upcoming OpenMetrics support.
The most notable difference between the Prometheus format and the OpenMetrics format is that OpenMetrics requires (not only recommends) `_total` suffix for counter metrics.
To ensure conformance to both formats in future, set `total` suffix to `counter` metrics. E.g.

```java
MetricValue m = new MetricValue();
m.name = "<NAME>"
m.suffix = "total";
m.type = "counter";
m.value = 1.0;
out.emit(m);
```

This will produce the following responses in respective formats:

* Prometheus (Special-cased to append `_total` to metric name in annotations, when `counter` has `total` suffix)

  ```
  # TYPE <NAME>_total counter
  <NAME>_total 1.0
  ```

* OpenMetrics

  ```
  # TYPE <NAME> counter
  <NAME>_total 1.0
  ```

All that said, if you prefer to leave the metrics `untyped` to keep configurations simple, that should be also fine.

#### Tips

* Prefer using rule pattern/condition, instead of `if` inside scripts. It's usually faster.
* Use `static` inside method-local inner class to do things that need to be done once, such as to compile a regex. Note that this should *not* be used to share mutable states because transform scripts are executed concurrently.
  ```yaml
    transform: |
      class Holder {
        public static final Pattern PATTERN = Pattern.compile(".*");
      }
      log(Holder.PATTERN.matcher("foo").matches());
  ```

### Debugging

Sometimes it's hard to debug complex `transform` scripts. Here are some tips and tricks to debug them.

#### Changing a log level to FINEST

This exporter uses JUL framework for logging. Errors caused by user configurations are logged at &gt;= INFO level. Other errors are
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
    V1.transform(in, out, "type");
```

Alternatively, you can also use `System.out.printf(...)` or `System.err.printf(...)` as in any other programs.


Benchmark
---------

First, it's almost impossible to do a fair comparison. The responses are not the same. Even the number of metrics is not the same.
Please also keep in mind that performance is highly dependent on the configurations and these numbers are very specific to the configurations we used for this benchmark.

See [examples/benchmark-kafka](examples/benchmark-kafka) for the setup details. Here are the results:

| Exporter | Config File (# of lines) | # of Metrics (\*1) | Throughput [req/s] | Avg. Latency [ms] <br/> @ 10 [req/s] |
|-|-|-|-|-|
| scriptable-jmx-exporter | [scriptable-jmx-exporter.yaml](examples/benchmark-kafka/scriptable-jmx-exporter.yaml) (54) | 3362 | 939.45 | TBD |
| jmx_exporter 0.13.0 | [kafka-2_0_0.yml](https://github.com/prometheus/jmx_exporter/blob/ce04b7dca8615d724d8f447fa25c44ae1c29238b/example_configs/kafka-2_0_0.yml) (103) | 3157 | 12.14 | TBD |

(\*) Benchmarked on Intel Core i5-9600K (with Turbo Boost disabled), Linux 5.7.4. (\*1) kafka-2_0_0.yml seems to be missing a number of metrics, such as `kafka.server:type=socket-server-metrics`.
We excluded such metrics as well. The difference in the number of metrics mostly comes from how we treat JVM metrics.

References
----------

- [Java Management Extensions (JMX) - Best Practices](http://www.oracle.com/technetwork/articles/java/best-practices-jsp-136021.html)


License
-------

The MIT License.
