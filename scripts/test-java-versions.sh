#!/bin/bash

set -euxo pipefail

exporter_jar=$1

trap "docker stop scriptable-jmx-exporter-test || true" EXIT

images=()
images+=(eclipse-temurin:8-jre-alpine)
images+=(openjdk:9-jre-slim)
images+=(openjdk:10-jre-slim)
images+=(eclipse-temurin:11-jre-alpine)
images+=(openjdk:12.0.2-jdk)
images+=(openjdk:13.0.2-slim)
images+=(openjdk:14.0.2-slim)
images+=(openjdk:15.0.2-slim)
images+=(openjdk:16.0.2-slim)
images+=(eclipse-temurin:17-jre-alpine)
images+=(eclipse-temurin:18-jre-alpine)
images+=(eclipse-temurin:19-jre-alpine)
images+=(eclipse-temurin:20-jre-alpine)

for image in ${images[@]}; do

	# start container
	docker run --rm -d -p 9639:9639 --name scriptable-jmx-exporter-test -v $(readlink -f $exporter_jar):/tmp/exporter.jar $image java -javaagent:/tmp/exporter.jar net.thisptr.jmx.exporter.tools.Pause

	# wait fot the container to be ready
	while true; do
		if [ $(docker logs scriptable-jmx-exporter-test 2>&1 | grep -c "INFO: Successfully started Scriptable JMX Exporter") -gt 0 ]; then # NOTE: grep -q will get EPIPE
			break
		fi
		sleep 1
	done

	# test
	if [ $(curl -sf 127.0.0.1:9639/metrics | grep -c scriptable_jmx_exporter_build_info) -eq 0 ]; then
		echo "$image: /metrics is not working" 1>&2
		exit 1
	fi

	# stop and delete container
	docker stop scriptable-jmx-exporter-test
done
