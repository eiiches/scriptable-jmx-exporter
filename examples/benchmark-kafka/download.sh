#!/bin/bash
set -euo pipefail

curl -LO https://raw.githubusercontent.com/prometheus/jmx_exporter/ce04b7dca8615d724d8f447fa25c44ae1c29238b/example_configs/kafka-2_0_0.yml
curl -LO https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.13.0/jmx_prometheus_javaagent-0.13.0.jar
