module local.movie.pushnotification {
    exports com.github.rahmnathan.localmovies.pushnotification.control;
    exports com.github.rahmnathan.localmovies.pushnotification.persistence;
    requires camel.core;
    requires directory.monitor;
    requires google.pushnotification;
    requires spring.data.commons;
    requires slf4j.api;
    requires javaee.api;
    requires spring.context;
}