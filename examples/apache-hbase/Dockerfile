FROM openjdk:14.0.2-slim

RUN DEBIAN_FRONTEND=noninteractive
RUN apt-get update -y && apt-get install -y curl procps iproute2
RUN curl -L https://downloads.apache.org/hbase/2.3.0/hbase-2.3.0-bin.tar.gz | tar xz --exclude "docs/*" -C /opt
ENV HBASE_HOME /opt/hbase-2.3.0
COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
