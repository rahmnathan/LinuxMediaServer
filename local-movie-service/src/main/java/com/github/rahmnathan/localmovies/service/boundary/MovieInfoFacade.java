package com.github.rahmnathan.localmovies.service.boundary;

import com.github.rahmnathan.localmovies.service.control.MovieInfoControl;
import com.github.rahmnathan.localmovies.persistence.data.MediaFile;
import com.github.rahmnathan.localmovies.service.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.service.filesystem.FileListProvider;

import javax.annotation.ManagedBean;
import java.util.*;

@ManagedBean
public class MovieInfoFacade {

    private final FileListProvider fileListProvider;
    private final MovieInfoControl movieInfoControl;

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
