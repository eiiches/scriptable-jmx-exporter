FROM eclipse-temurin:8u372-b07-jdk

RUN DEBIAN_FRONTEND=noninteractive
RUN apt-get update -y && apt-get install -y curl procps
RUN curl -L https://downloads.apache.org/flume/1.11.0/apache-flume-1.11.0-bin.tar.gz | tar xz -C /opt
ENV FLUME_HOME /opt/apache-flume-1.11.0-bin
COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
