module local.movie.service {
    exports com.github.rahmnathan.localmovies.service.boundary;
    exports com.github.rahmnathan.localmovies.service.data;
    requires spring.context;
    requires spring.beans;
    requires local.movie.info.persistence;
    requires directory.monitor;
    requires java.logging;
    requires movie.info.api;
    requires guava;
    requires movie.info.omdb;
}
