package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.directory.monitor.DirectoryMonitor;
import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieClient;
import com.github.rahmnathan.localmovies.data.MovieOrder;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.filesystem.FileListProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private final DirectoryMonitor directoryMonitor;
    private final FileListProvider fileListProvider;

    @Autowired
    public MovieInfoControl(MovieInfoProvider movieInfoProvider, Collection<DirectoryMonitorObserver> observers, FileListProvider fileListProvider) {
        this.directoryMonitor = new DirectoryMonitor(observers);
        this.movieInfoProvider = movieInfoProvider;
        this.fileListProvider = fileListProvider;
    }

    @PostConstruct
    public void startDirectoryMonitor() {
        Arrays.stream(mediaPaths).forEach(directoryMonitor::registerDirectory);
    }

    public MediaFile loadSingleMovie(String filePath) {
        return movieInfoProvider.loadMediaInfo(filePath);
    }

    public int loadMovieListLength(String relativePath) {
        int total = 0;
        for (String mediaPath : mediaPaths) {
            total += fileListProvider.listFiles(mediaPath + relativePath).length;
        }
        return total;
    }

    public List<MediaFile> loadMovieList(MovieSearchCriteria searchCriteria) {
        Set<String> files = listFiles(searchCriteria.getPath());
        List<MediaFile> movies = loadMediaInfo(files);

        if (searchCriteria.getClient() == MovieClient.WEBAPP) {
            movies = removePosterImages(movies);
        }

        movies = sortMediaFiles(searchCriteria, movies);
        return paginateMediaFiles(movies, searchCriteria);
    }

    private List<MediaFile> paginateMediaFiles(List<MediaFile> movies, MovieSearchCriteria searchCriteria){
        return movies.stream()
                .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                .limit(searchCriteria.getItemsPerPage())
                .collect(Collectors.toList());
    }

    private List<MediaFile> sortMediaFiles(MovieSearchCriteria searchCriteria, List<MediaFile> movies){
        if (searchCriteria.getPath().split(File.separator).length > 1) {
            return MovieUtils.sortMovieInfoList(movies, MovieOrder.SEASONS_EPISODES);
        } else if (searchCriteria.getOrder() != null) {
            return MovieUtils.sortMovieInfoList(movies, searchCriteria.getOrder());
        }

        return movies;
    }

    private List<MediaFile> removePosterImages(List<MediaFile> movies){
        logger.info("Removing images for webapp");
        return movies.stream()
                .map(MediaFile.Builder::copyWithNoImage)
                .collect(Collectors.toList());
    }

    private List<MediaFile> loadMediaInfo(Set<String> files){
        return files.parallelStream()
                .sorted()
                .map(movieInfoProvider::loadMediaInfo)
                .collect(Collectors.toList());
    }

    private Set<String> listFiles(String path) {
        Set<String> files = new HashSet<>();
        Arrays.stream(mediaPaths)
                .forEach(mediaPath -> files.addAll(Arrays.stream(fileListProvider.listFiles(mediaPath + path))
                        .map(file -> file.getAbsolutePath().substring(mediaPath.length()))
                        .collect(Collectors.toList()))
                );

        return files;
    }
}
