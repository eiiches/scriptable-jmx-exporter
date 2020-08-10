Example: Apache Flume
=====================

### Run

```console
$ docker-compose up
```

### Response Example

```console
$ curl -s "localhost:9639/metrics?include_timestamp=false&include_help=false" | grep apache_flume
org_apache_flume_channel_ChannelCapacity{type="c1",} 1000
org_apache_flume_channel_ChannelFillPercentage{type="c1",} 0
org_apache_flume_channel_ChannelSize{type="c1",} 0
org_apache_flume_channel_EventPutAttemptCount{type="c1",} 0
org_apache_flume_channel_EventPutSuccessCount{type="c1",} 0
org_apache_flume_channel_EventTakeAttemptCount{type="c1",} 12
org_apache_flume_channel_EventTakeSuccessCount{type="c1",} 0
org_apache_flume_channel_StartTime{type="c1",} 1596876865812
org_apache_flume_channel_StopTime{type="c1",} 0
```
