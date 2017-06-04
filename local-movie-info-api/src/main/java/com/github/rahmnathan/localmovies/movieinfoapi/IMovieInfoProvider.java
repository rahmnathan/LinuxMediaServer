package com.github.rahmnathan.localmovies.movieinfoapi;

@FunctionalInterface
public interface IMovieInfoProvider {

    MovieInfo loadMovieInfo(String title);
}
