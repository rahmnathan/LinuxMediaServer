package com.github.rahmnathan.localmovies.service.control;

import com.github.rahmnathan.localmovies.persistence.data.MediaFile;
import com.github.rahmnathan.localmovies.service.data.MovieClient;
import com.github.rahmnathan.localmovies.service.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.service.utils.MediaFileUtils;

import javax.annotation.ManagedBean;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
public class MovieInfoControl {
    private final MovieInfoProvider movieInfoProvider;
    private final MediaFileUtils mediaFileUtils;

    public MovieInfoControl(MovieInfoProvider movieInfoProvider, MediaFileUtils mediaFileUtils) {
        this.movieInfoProvider = movieInfoProvider;
        this.mediaFileUtils = mediaFileUtils;
    }

    public MediaFile loadSingleMovie(String filePath) {
        return movieInfoProvider.loadMediaInfo(filePath);
    }

    public List<MediaFile> loadMediaFileList(MovieSearchCriteria searchCriteria, Set<String> files) {
        List<MediaFile> movies = loadMediaInfo(files);

        if (searchCriteria.getClient() == MovieClient.WEBAPP) {
            movies = mediaFileUtils.removePosterImages(movies);
        }

        movies = mediaFileUtils.sortMediaFiles(searchCriteria, movies);
        return mediaFileUtils.paginateMediaFiles(movies, searchCriteria);
    }

    private List<MediaFile> loadMediaInfo(Set<String> relativePaths){
        return relativePaths.parallelStream()
                .sorted()
                .map(movieInfoProvider::loadMediaInfo)
                .collect(Collectors.toList());
    }
}
