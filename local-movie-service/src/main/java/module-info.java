module com.github.rahmnathan.local.movie.service {
    requires spring.context;
    requires spring.beans;
    requires com.github.rahmnathan.local.movie.video.converter;
    requires com.github.rahmnathan.local.movie.info.provider;
    requires java.logging;
    requires com.github.rahmnathan.directory.monitor;
    requires java.xml.ws.annotation;
    requires com.github.rahmnathan.movie.info.api;
    requires com.github.rahmnathan.movie.info.omdb;
    requires guava;
    exports com.github.rahmnathan.localmovies.service.boundary;
    exports com.github.rahmnathan.localmovies.service.control;
    exports com.github.rahmnathan.localmovies.service.data;
}