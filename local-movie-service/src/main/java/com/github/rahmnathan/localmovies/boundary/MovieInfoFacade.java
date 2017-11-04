package com.github.rahmnathan.localmovies.boundary;

import com.github.rahmnathan.localmovies.control.MovieInfoControl;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.filesystem.FileListProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MovieInfoFacade {

    private final FileListProvider fileListProvider;
    private final MovieInfoControl movieInfoControl;

    @Autowired
    public MovieInfoFacade(MovieInfoControl movieInfoControl, FileListProvider fileListProvider){
        this.movieInfoControl = movieInfoControl;
        this.fileListProvider = fileListProvider;
    }

    public int loadMovieListLength(String directoryPath){
        return fileListProvider.listFiles(directoryPath).size();
    }

    public List<MediaFile> loadMovieList(MovieSearchCriteria searchCriteria) {
        Set<String> filePaths = fileListProvider.listFiles(searchCriteria.getPath());
        return movieInfoControl.loadMediaFileList(searchCriteria, filePaths);
    }

    public MediaFile loadSingleMovie(String path) {
        return movieInfoControl.loadSingleMovie(path);
    }

}
