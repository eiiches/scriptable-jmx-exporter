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

sed -i "s;http://central.maven.org/maven2/net/thisptr/java-prometheus-metrics-agent/[0-9a-z.-]*/java-prometheus-metrics-agent-[0-9a-z.-]*.jar;http://central.maven.org/maven2/net/thisptr/java-prometheus-metrics-agent/$version/java-prometheus-metrics-agent-$version.jar;" README.md
