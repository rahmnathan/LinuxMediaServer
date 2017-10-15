package com.github.rahmnathan.localmovies.service.boundary;

import com.github.rahmnathan.localmovies.service.control.MovieInfoControl;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.service.data.MovieSearchCriteria;
import com.github.rahmnathan.video.monitor.VideoConversionMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieInfoFacade {
    private final MovieInfoControl movieInfoControl;

    @Autowired
    public MovieInfoFacade(MovieInfoControl movieInfoControl){
        this.movieInfoControl = movieInfoControl;
    }

    public int loadMovieListLength(String directoryPath){
        return movieInfoControl.loadMovieListLength(directoryPath);
    }

    public List<MediaFile> loadMovieList(MovieSearchCriteria searchCriteria) {
        return movieInfoControl.loadMovieList(searchCriteria);
    }

    public MediaFile loadSingleMovie(String path) {
        return movieInfoControl.loadSingleMovie(path);
    }

    public boolean hasUpdates(String date){
        return movieInfoControl.hasUpdates(date);
    }
}
