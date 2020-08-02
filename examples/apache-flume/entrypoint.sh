#!/bin/bash
set -euo pipefail

exec java ${JAVA_OPTS:-} -cp $FLUME_HOME/conf:$FLUME_HOME/lib/*:/lib/* -Djava.library.path= -Dflume.root.logger=INFO,console org.apache.flume.node.Application -f $FLUME_HOME/conf/flume-conf.properties -n agent --no-reload-conf
