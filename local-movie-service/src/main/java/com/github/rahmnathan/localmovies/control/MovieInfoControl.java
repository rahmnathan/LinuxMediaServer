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

        // List all files in provided path
        Arrays.stream(mediaPaths)
                .forEach(path -> relativeMoviePaths.addAll(Arrays.stream(fileListProvider.listFiles(path + searchCriteria.getPath()))
                        .map(file -> file.getAbsolutePath().substring(path.length()))
                        .collect(Collectors.toList())));

        // Load movie info
        List<MovieInfo> movies = relativeMoviePaths.parallelStream()
                    .sorted()
                    .map(movieInfoProvider::loadMovieInfoFromCache)
                    .collect(Collectors.toList());

        if(searchCriteria.getClient() == MovieClient.WEBAPP){
            // Removing images for web app
            movies = movies.stream()
                    .map(MovieInfo.Builder::copyWithNoImage)
                    .collect(Collectors.toList());
        }

        // Sorting
        if(searchCriteria.getPath().split(File.separator).length > 1)
            movies = sortMovieInfoList(movies, MovieOrder.SEASONS_EPISODES);
        else if (searchCriteria.getOrder() != null)
            movies = sortMovieInfoList(movies, searchCriteria.getOrder());

        // Pagination
        return movies.stream()
                .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                .limit(searchCriteria.getItemsPerPage())
                .collect(Collectors.toList());
    }

    private List<MovieInfo> sortMovieInfoList(List<MovieInfo> movieInfoList, MovieOrder order){
        switch (order){
            case DATE_ADDED:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Long.compare(movie2.getDateCreated(), movie1.getDateCreated()))
                        .collect(Collectors.toList());
            case MOST_VIEWS:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Integer.compare(movie2.getViews(), movie1.getViews()))
                        .collect(Collectors.toList());
            case RELEASE_YEAR:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Long.valueOf(movie2.getReleaseYear()).compareTo(Long.valueOf(movie1.getReleaseYear())))
                        .collect(Collectors.toList());
            case RATING:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Double.valueOf(movie2.getIMDBRating()).compareTo(Double.valueOf(movie1.getIMDBRating())))
                        .collect(Collectors.toList());
            case SEASONS_EPISODES:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> {
                            Integer current = Integer.parseInt(movie1.getTitle().split(" ")[1].split("\\.")[0]);
                            Integer next = Integer.parseInt(movie2.getTitle().split(" ")[1].split("\\.")[0]);
                            return current.compareTo(next);
                        })
                        .collect(Collectors.toList());
        }
        return movieInfoList;
    }
}
