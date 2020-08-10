Example: Apache Cassandra
=========================

There are two variations: `scriptable-jmx-exporter-untyped.yaml` and `scriptable-jmx-exporter-typed.yaml`.
The former is a simpler configuration which just exposes all metrics as `untyped` value.
On the other hand, the latter converts an MBean into `summary`, `counter` or `gauge` where possible.

### Run

```console
$ docker-compose up
```

### Response Example

#### scriptable-jmx-exporter-untyped.yaml

```console
$ curl -s "localhost:9639/metrics?include_timestamp=false&include_help=false" | awk '/^org_apache_cassandra/'
```

See [scriptable-jmx-exporter-untyped.out](scriptable-jmx-exporter-untyped.out).

#### scriptable-jmx-exporter-typed.yaml

```console
$ curl -s "localhost:9639/metrics?include_timestamp=false&include_help=false" | awk '/^org_apache_cassandra/'
```

See [scriptable-jmx-exporter-typed.out](scriptable-jmx-exporter-typed.out).
