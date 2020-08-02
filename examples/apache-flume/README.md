Example: Apache Flume
=====================

### Run

```console
$ docker-compose up
```

### Response Example

```console
$ curl -s localhost:9639/metrics | awk '!/^#/ && /apache_flume/'
org_apache_flume_channel_channel_capacity{type="c1",} 1000
org_apache_flume_channel_channel_fill_percentage{type="c1",} 0
org_apache_flume_channel_channel_size{type="c1",} 0
org_apache_flume_channel_event_put_attempt_count{type="c1",} 2
org_apache_flume_channel_event_put_success_count{type="c1",} 2
org_apache_flume_channel_event_take_attempt_count{type="c1",} 11
org_apache_flume_channel_event_take_success_count{type="c1",} 2
org_apache_flume_channel_start_time{type="c1",} 1596383067434
org_apache_flume_channel_stop_time{type="c1",} 0
```
