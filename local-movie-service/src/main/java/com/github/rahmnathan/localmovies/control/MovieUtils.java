package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieOrder;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class MovieUtils {
    private static final Logger logger = Logger.getLogger(MovieUtils.class.getName());

    static boolean isTopLevel(String currentPath){
        return currentPath.split(File.separator).length == 2;
    }

    static File getParentFile(String path){
        int directoryDepth = path.split(File.separator).length;
        if(!isTopLevel(path))
            directoryDepth -= 2;

        File file = new File(path);
        for(int i = 0; i < directoryDepth; i++){
            file = file.getParentFile();
        }

        return file;
    }

    static String getTitle(String fileName){
        if (fileName.charAt(fileName.length() - 4) == '.') {
            return fileName.substring(0, fileName.length() - 4);
        }

        return fileName;
    }

    static List<MediaFile> sortMovieInfoList(List<MediaFile> movieInfoList, MovieOrder order){
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
