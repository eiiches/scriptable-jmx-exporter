Example: Custom JVM Metrics + Prometheus &amp; Grafana
======================================================

### Run

```console
$ docker-compose up
```

* Grafana: [http://localhost:3000](http://localhost:3000) (admin:admin)
* Prometheus: [http://localhost:9090](http://localhost:9090)
* Exporter: [http://localhost:9639/metrics](http://localhost:9639/metrics)

### Response Example

```console
$ curl -s "localhost:9639/metrics?include_timestamp=false" | grep Version
java_lang_OperatingSystem_Version_info{version="5.7.4-arch1-1",} 1
java_lang_Runtime_VmVersion_info{version="14.0.1+7",} 1
$ curl -s "localhost:9639/metrics?include_timestamp=false" | grep ThreadStateCount
# HELP java_lang_Threading_ThreadStateCount The number of threads in the state. This value is calculated from ThreadMXBean#getThreadInfo(java.lang:type=Threading:AllThreadIds).
# TYPE java_lang_Threading_ThreadStateCount gauge
java_lang_Threading_ThreadStateCount{state="NEW",} 0
java_lang_Threading_ThreadStateCount{state="RUNNABLE",} 11
java_lang_Threading_ThreadStateCount{state="BLOCKED",} 0
java_lang_Threading_ThreadStateCount{state="WAITING",} 1
java_lang_Threading_ThreadStateCount{state="TIMED_WAITING",} 3
java_lang_Threading_ThreadStateCount{state="TERMINATED",} 0
```
