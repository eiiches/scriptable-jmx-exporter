#!/bin/bash
set -euo pipefail

if [ "$#" -ne 1 ]; then
	echo "Usage: $0 version" 1>&2
	exit 1
fi

scriptpath="$(readlink -f "$0")"
scriptdir="$(dirname "$scriptpath")"
cd "$scriptdir/.."

version="$1"

sed -i "s;https://repo1.maven.org/maven2/net/thisptr/java-prometheus-metrics-agent/[0-9a-z.-]*/java-prometheus-metrics-agent-[0-9a-z.-]*.jar;https://repo1.maven.org/maven2/net/thisptr/scriptable-jmx-exporter/$version/scriptable-jmx-exporter-$version.jar;g" README.md
sed -i "s;https://repo1.maven.org/maven2/net/thisptr/scriptable-jmx-exporter/[0-9a-z.-]*/scriptable-jmx-exporter-[0-9a-z.-]*.jar;https://repo1.maven.org/maven2/net/thisptr/scriptable-jmx-exporter/$version/scriptable-jmx-exporter-$version.jar;g" README.md
sed -i "s;https://raw.githubusercontent.com/eiiches/scriptable-jmx-exporter/v[0-9a-z.-]*/;https://raw.githubusercontent.com/eiiches/scriptable-jmx-exporter/v$version/;g" README.md
sed -i "s;scriptable-jmx-exporter-[0-9a-z.-]*.jar;scriptable-jmx-exporter-$version.jar;g" README.md
