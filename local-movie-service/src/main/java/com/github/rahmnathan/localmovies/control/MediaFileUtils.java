package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieOrder;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
class MediaFileUtils {
    private static final Logger logger = Logger.getLogger(MediaFileUtils.class.getName());

    List<MediaFile> sortMediaFiles(MovieSearchCriteria searchCriteria, List<MediaFile> mediaFiles){
        logger.info("Sorting movie list - order: " + searchCriteria.getOrder());
        if (searchCriteria.getPath().split(File.separator).length > 1) {
            return sortMovieInfoList(mediaFiles, MovieOrder.SEASONS_EPISODES);
        } else if (searchCriteria.getOrder() != null) {
            return sortMovieInfoList(mediaFiles, searchCriteria.getOrder());
        }

        return mediaFiles;
    }

    List<MediaFile> paginateMediaFiles(List<MediaFile> mediaFiles, MovieSearchCriteria searchCriteria){
        logger.info("Paginating movie list - page: " + searchCriteria.getPage() + " resultsPerPage: " + searchCriteria.getItemsPerPage());
        return mediaFiles.stream()
                .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                .limit(searchCriteria.getItemsPerPage())
                .collect(Collectors.toList());
    }

    List<MediaFile> removePosterImages(List<MediaFile> mediaFiles){
        logger.info("Removing images for webapp");
        return mediaFiles.stream()
                .map(MediaFile.Builder::copyWithNoImage)
                .collect(Collectors.toList());
    }

    private List<MediaFile> sortMovieInfoList(List<MediaFile> movieInfoList, MovieOrder order){
        logger.info("Sorting movie list: " + order.name());
        switch (order){
            case DATE_ADDED:
                return movieInfoList.stream()
                        .sorted((movie1, movie2) -> Long.compare(movie2.getDateCreated(), movie1.getDateCreated()))
                        .collect(Collectors.toList());
            case MOST_VIEWS:
                return movieInfoList.stream()
                        .sorted((movie1, movie2) -> Integer.compare(movie2.getViews(), movie1.getViews()))
                        .collect(Collectors.toList());
            case RELEASE_YEAR:
                return movieInfoList.stream()
                        .sorted((movie1, movie2) -> Long.valueOf(movie2.getMovieInfo().getReleaseYear())
                                .compareTo(Long.valueOf(movie1.getMovieInfo().getReleaseYear())))
                        .collect(Collectors.toList());
            case RATING:
                return movieInfoList.stream()
                        .sorted((movie1, movie2) -> Double.valueOf(movie2.getMovieInfo().getIMDBRating())
                                .compareTo(Double.valueOf(movie1.getMovieInfo().getIMDBRating())))
                        .collect(Collectors.toList());
            case SEASONS_EPISODES:
                return movieInfoList.stream()
                        .sorted((movie1, movie2) -> {
                            Integer current = Integer.parseInt(movie1.getMovieInfo().getTitle().split(" ")[1]);
                            Integer next = Integer.parseInt(movie2.getMovieInfo().getTitle().split(" ")[1]);
                            return current.compareTo(next);
                        })
                        .collect(Collectors.toList());
        }
        return movieInfoList;
    }
}
