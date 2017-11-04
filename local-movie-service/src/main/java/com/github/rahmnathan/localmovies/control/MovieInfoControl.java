package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieClient;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.utils.MediaFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MovieInfoControl {
    @Value("${media.path}")
    private String[] mediaPaths;
    private final MovieInfoProvider movieInfoProvider;
    private final MediaFileUtils mediaFileUtils;

    @Autowired
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
