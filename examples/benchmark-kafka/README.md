benchmark-kafka
===============

1. Start containers

   ```sh
   ./download.sh
   docker-compose up
   ```

2. Verify setup

   ```sh
   curl http://localhost:9639/metrics # scriptable-jmx-exporter
   curl http://localhost:9404/metrics # jmx_exporter
   ```

3. Apply load

   ```sh
   CONTAINER_IP=$(docker inspect benchmark-kafka_kafka_1 -f '{{ with (index .NetworkSettings.Networks "benchmark-kafka_default") }}{{ .IPAddress }}{{ end }}')
   for i in {0..9}; do wrk -d 10 -t `nproc` -c `nproc` "http://$CONTAINER_IP:9639/metrics"; done
   for i in {0..9}; do wrk -d 10 -t `nproc` -c `nproc` "http://$CONTAINER_IP:9404/metrics"; done
   ```
