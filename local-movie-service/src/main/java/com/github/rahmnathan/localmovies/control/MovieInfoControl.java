package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.directorymonitor.DirectoryMonitor;
import com.github.rahmnathan.localmovies.data.MovieClient;
import com.github.rahmnathan.localmovies.data.MovieOrder;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieInfoControl {
    @Value("${media.path}")
    private String[] mediaPaths;
    private final MovieInfoProvider movieInfoProvider;
    private final DirectoryMonitor directoryMonitor;
    private final FileListProvider fileListProvider;

    @Autowired
    public MovieInfoControl(MovieInfoProvider movieInfoProvider, DirectoryMonitor directoryMonitor, FileListProvider fileListProvider){
        this.movieInfoProvider = movieInfoProvider;
        this.directoryMonitor = directoryMonitor;
        this.fileListProvider = fileListProvider;
    }

    @PostConstruct
    public void startDirectoryMonitor() {
        Arrays.stream(mediaPaths).forEach(directoryMonitor::registerDirectory);
    }

    public MovieInfo loadSingleMovie(String filePath) {
        return movieInfoProvider.loadMovieInfoFromCache(filePath);
    }

    public boolean hasUpdates(String date){
        return fileListProvider.hasUpdates(date);
    }

    public int loadMovieListLength(String relativePath){
        int total = 0;
        for(String mediaPath : mediaPaths){
            total += fileListProvider.listFiles(mediaPath + relativePath).length;
        }
        return total;
    }

    public List<MovieInfo> loadMovieList(MovieSearchCriteria searchCriteria) {
        List<String> relativeMoviePaths = new ArrayList<>();
        Arrays.stream(mediaPaths)
                .forEach(path -> relativeMoviePaths.addAll(Arrays.stream(fileListProvider.listFiles(path + searchCriteria.getPath()))
                        .map(file -> file.getAbsolutePath().substring(path.length()))
                        .collect(Collectors.toList())));

        int pathLength = searchCriteria.getPath().split(File.separator).length;
        List<MovieInfo> movies;
        if(pathLength > 1){
            movies = relativeMoviePaths.parallelStream()
                    .sorted((movieInfo, t1) -> {
                        Integer current = Integer.parseInt(movieInfo.split(File.separator)[pathLength].split(" ")[1].split("\\.")[0]);
                        Integer next = Integer.parseInt(t1.split(File.separator)[pathLength].split(" ")[1].split("\\.")[0]);
                        return current.compareTo(next);
                    })
                    .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                    .limit(searchCriteria.getItemsPerPage())
                    .map(movieInfoProvider::loadMovieInfoFromCache)
                    .collect(Collectors.toList());
        } else {
            movies = relativeMoviePaths.parallelStream()
                    .sorted()
                    .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                    .limit(searchCriteria.getItemsPerPage())
                    .map(movieInfoProvider::loadMovieInfoFromCache)
                    .collect(Collectors.toList());
        }

        if(searchCriteria.getClient() == MovieClient.WEBAPP){
            movies = movies.stream()
                    .map(MovieInfo.Builder::copyWithNoImage)
                    .collect(Collectors.toList());
        }

        return movies;
    }

    public List<MovieInfo> sortMovieInfoList(List<MovieInfo> movieInfoList, String orderString){
        MovieOrder order = MovieOrder.valueOf(orderString);
        switch (order){
            case DATE_ADDED:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Long.valueOf(movie2.getDateCreated()).compareTo(movie1.getDateCreated()))
                        .collect(Collectors.toList());
            case MOST_VIEWS:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Integer.valueOf(movie2.getViews()).compareTo(movie1.getViews()))
                        .collect(Collectors.toList());
            case RELEASE_YEAR:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Long.valueOf(movie2.getReleaseYear()).compareTo(Long.valueOf(movie1.getReleaseYear())))
                        .collect(Collectors.toList());
            case RATING:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Double.valueOf(movie2.getIMDBRating()).compareTo(Double.valueOf(movie1.getIMDBRating())))
                        .collect(Collectors.toList());
        }
        return movieInfoList;
    }
}
