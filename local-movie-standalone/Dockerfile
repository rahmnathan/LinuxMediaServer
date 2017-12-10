FROM openjdk:9-jre-slim

MAINTAINER nathan

RUN mkdir /opt/localmovies && mkdir /opt/localmovies/config

ADD src/main/resources/ssl-gen.sh /opt/localmovies/ssl-gen.sh
RUN /opt/localmovies/ssl-gen.sh

RUN apt-get update && \
    apt-get -y install ffmpeg && \
    apt-get clean

ARG JAR_FILE
ADD target/$JAR_FILE /opt/localmovies/localmovies.jar

EXPOSE 5432

WORKDIR /opt/localmovies/
ENTRYPOINT java --add-modules java.xml.bind -Xms64m -Xmx3G -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=3G -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -jar localmovies.jar