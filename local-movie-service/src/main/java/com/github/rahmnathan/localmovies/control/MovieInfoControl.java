package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieClient;
import com.github.rahmnathan.localmovies.data.MovieOrder;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.filesystem.FileListProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class MovieInfoControl {
    @Value("${media.path}")
    private String[] mediaPaths;
    private final Logger logger = Logger.getLogger(MovieInfoControl.class.getName());
    private final MovieInfoProvider movieInfoProvider;

    @Autowired
    public MovieInfoControl(MovieInfoProvider movieInfoProvider) {
        this.movieInfoProvider = movieInfoProvider;
    }

    public MediaFile loadSingleMovie(String filePath) {
        return movieInfoProvider.loadMediaInfo(filePath);
    }

    public List<MediaFile> loadMediaFileList(MovieSearchCriteria searchCriteria, Set<String> files) {
        List<MediaFile> movies = loadMediaInfo(files);

        if (searchCriteria.getClient() == MovieClient.WEBAPP) {
            movies = removePosterImages(movies);
        }

        movies = sortMediaFiles(searchCriteria, movies);
        return paginateMediaFiles(movies, searchCriteria);
    }

    private List<MediaFile> loadMediaInfo(Set<String> relativePaths){
        return relativePaths.parallelStream()
                .sorted()
                .map(movieInfoProvider::loadMediaInfo)
                .collect(Collectors.toList());
    }

    private List<MediaFile> sortMediaFiles(MovieSearchCriteria searchCriteria, List<MediaFile> mediaFiles){
        logger.info("Sorting movie list - order: " + searchCriteria.getOrder());
        if (searchCriteria.getPath().split(File.separator).length > 1) {
            return MovieUtils.sortMovieInfoList(mediaFiles, MovieOrder.SEASONS_EPISODES);
        } else if (searchCriteria.getOrder() != null) {
            return MovieUtils.sortMovieInfoList(mediaFiles, searchCriteria.getOrder());
        }

        return mediaFiles;
    }

    private List<MediaFile> paginateMediaFiles(List<MediaFile> mediaFiles, MovieSearchCriteria searchCriteria){
        logger.info("Paginating movie list - page: " + searchCriteria.getPage() + " resultsPerPage: " + searchCriteria.getItemsPerPage());
        return mediaFiles.stream()
                .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                .limit(searchCriteria.getItemsPerPage())
                .collect(Collectors.toList());
    }

    private List<MediaFile> removePosterImages(List<MediaFile> mediaFiles){
        logger.info("Removing images for webapp");
        return mediaFiles.stream()
                .map(MediaFile.Builder::copyWithNoImage)
                .collect(Collectors.toList());
    }
}
