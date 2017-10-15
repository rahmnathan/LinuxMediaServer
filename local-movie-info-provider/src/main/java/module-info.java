module com.github.rahmnathan.local.movie.info.provider {
    requires com.github.rahmnathan.movie.info.api;
    requires spring.context;
    requires spring.data.commons;
    requires hibernate.jpa;
    exports com.github.rahmnathan.localmovies.data;
    exports com.github.rahmnathan.localmovies.persistence;
}