#!/bin/bash
set -euo pipefail

if [ $# -eq 0 ]; then
  ipv4=$(ip a s eth0 | awk -F'[ /]' '/inet /{print $6}')

  cat > $HBASE_HOME/conf/hbase-site.xml <<-EOF
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
  <property><name>hbase.cluster.distributed</name><value>false</value></property>
  <property><name>hbase.tmp.dir</name><value>./tmp</value></property>
  <property><name>hbase.unsafe.stream.capability.enforce</name><value>false</value></property>
  <property><name>hbase.regionserver.hostname</name><value>$ipv4</value></property>
  <property><name>hbase.master.hostname</name><value>$ipv4</value></property>
</configuration>
EOF

  HBASE_OPTS="$HBASE_DAEMON_OPTS" exec $HBASE_HOME/bin/hbase --config $HBASE_HOME/conf master start
else
  exec $HBASE_HOME/bin/hbase --config $HBASE_HOME/conf "$@"
fi
