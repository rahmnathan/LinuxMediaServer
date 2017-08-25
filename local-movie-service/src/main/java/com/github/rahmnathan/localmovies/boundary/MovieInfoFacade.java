package com.github.rahmnathan.localmovies.boundary;

import com.github.rahmnathan.localmovies.control.MovieInfoControl;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import com.github.rahmnathan.video.converter.VideoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieInfoFacade {
    private final VideoConverter videoController;
    private final MovieInfoControl movieInfoControl;

    @Autowired
    public MovieInfoFacade(MovieInfoControl movieInfoControl, VideoConverter videoController){
        this.movieInfoControl = movieInfoControl;
        this.videoController = videoController;
    }

    public int loadMovieListLength(String directoryPath){
        return movieInfoControl.loadMovieListLength(directoryPath);
    }

    public List<MovieInfo> loadMovieList(MovieSearchCriteria searchCriteria) {
        return movieInfoControl.loadMovieList(searchCriteria);
    }

    public MovieInfo loadSingleMovie(String path) {
        return movieInfoControl.loadSingleMovie(path);
    }

    public boolean hasUpdates(String date){
        return movieInfoControl.hasUpdates(date);
    }
}
