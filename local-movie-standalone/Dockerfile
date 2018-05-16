FROM ubuntu:18.04

MAINTAINER nathan

RUN apt-get update && \
    apt-get -y install apt-utils && \
    apt-get -y install ffmpeg && \
    apt-get -y install handbrake-cli && \
    apt-get clean

RUN mkdir /opt/localmovies && mkdir /opt/localmovies/config

RUN apt-get update && \
    apt-get -y install software-properties-common && \
    apt-get -y install openjdk-8-jre

#    add-apt-repository -y ppa:linuxuprising/java && \
#    yes | apt-get -y install oracle-java10-installer && \
#    apt-get -y install gnupg oracle-java10-set-default

RUN echo "deb http://ppa.launchpad.net/stebbins/handbrake-releases/ubuntu bionic main" >> /etc/apt/sources.list && \
    echo "deb-src http://ppa.launchpad.net/stebbins/handbrake-releases/ubuntu bionic main" >> /etc/apt/sources.list && \
    apt-key adv --recv-keys --keyserver keyserver.ubuntu.com 8771ADB0816950D8

ADD src/main/resources/vault.cer /opt/localmovies/vault.cer
RUN keytool -importcert -file /opt/localmovies/vault.cer -keystore /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -storepass changeit -noprompt -alias "vault"

ARG JAR_FILE
ADD target/$JAR_FILE /opt/localmovies/localmovies.jar

EXPOSE 5432

WORKDIR /opt/localmovies/
ENTRYPOINT java -jar localmovies.jar