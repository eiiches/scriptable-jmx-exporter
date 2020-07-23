#!/bin/bash

rm -f /tmp/perf-1.map
java -cp /attach-main.jar net.virtualvoid.perf.AttachOnce 1
sed -i 's/;::/::/g' /tmp/perf-1.map
cat /tmp/perf-1.map
