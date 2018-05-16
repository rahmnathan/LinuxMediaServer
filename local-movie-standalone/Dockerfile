FROM openjdk:10-jre-slim

MAINTAINER nathan

RUN mkdir /opt/localmovies && mkdir /opt/localmovies/config

ADD src/main/resources/ssl-gen.sh /opt/localmovies/ssl-gen.sh
RUN /opt/localmovies/ssl-gen.sh

RUN apt-get update && \
    apt-get -y install software-properties-common && \
    apt-get -y install gnupg

RUN echo "deb http://ppa.launchpad.net/stebbins/handbrake-releases/ubuntu bionic main" >> /etc/apt/sources.list && \
    echo "deb-src http://ppa.launchpad.net/stebbins/handbrake-releases/ubuntu bionic main" >> /etc/apt/sources.list && \
    apt-key adv --recv-keys --keyserver keyserver.ubuntu.com 8771ADB0816950D8

RUN apt-get update && \
    apt-get -y install apt-utils && \
    apt-get -y install ffmpeg && \
    apt-get -y install handbrake-cli && \
    apt-get clean

ADD src/main/resources/vault.cer /opt/localmovies/vault.cer
RUN keytool -importcert -file /opt/localmovies/vault.cer -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt -alias "vault"

ARG JAR_FILE
ADD target/$JAR_FILE /opt/localmovies/localmovies.jar

EXPOSE 5432

WORKDIR /opt/localmovies/
ENTRYPOINT java --add-modules java.xml.bind -jar localmovies.jar