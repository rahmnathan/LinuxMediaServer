module local.movie.web {
    requires tomcat.embed.core;
    requires spring.web;
    requires spring.beans;
    requires local.movie.service;
    requires local.movie.pushnotification;
    requires local.movie.info.persistence;
    requires movie.info.api;
    requires slf4j.api;
}
