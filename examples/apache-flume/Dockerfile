FROM openjdk:14.0.2-slim

RUN DEBIAN_FRONTEND=noninteractive
RUN apt-get update -y && apt-get install -y curl procps
RUN curl -L https://downloads.apache.org/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz | tar xz -C /opt
ENV FLUME_HOME /opt/apache-flume-1.9.0-bin
COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
