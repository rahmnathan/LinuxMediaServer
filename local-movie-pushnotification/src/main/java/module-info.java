module local.movie.pushnotification {
    exports com.github.rahmnathan.localmovies.pushnotification.control;
    exports com.github.rahmnathan.localmovies.pushnotification.persistence;
    requires spring.context;
    requires camel.core;
    requires directory.monitor;
    requires java.logging;
    requires spring.beans;
    requires google.pushnotification;
    requires spring.data.commons;
}