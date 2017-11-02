package com.github.rahmnathan.localmovies.boundary;

import com.github.rahmnathan.directory.monitor.DirectoryMonitor;
import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.localmovies.control.MovieInfoControl;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.filesystem.FileListProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MovieInfoFacade {
    @Value("${media.path}")
    private String[] mediaPaths;
    private final FileListProvider fileListProvider;
    private final MovieInfoControl movieInfoControl;
    private final DirectoryMonitor directoryMonitor;

    @Autowired
    public MovieInfoFacade(MovieInfoControl movieInfoControl, FileListProvider fileListProvider, Collection<DirectoryMonitorObserver> observers){
        this.directoryMonitor = new DirectoryMonitor(observers);
        this.movieInfoControl = movieInfoControl;
        this.fileListProvider = fileListProvider;
    }

    @PostConstruct
    public void startDirectoryMonitor() {
        Arrays.stream(mediaPaths).forEach(directoryMonitor::registerDirectory);
    }

    public int loadMovieListLength(String directoryPath){
        return listFiles(directoryPath).size();
    }

    public List<MediaFile> loadMovieList(MovieSearchCriteria searchCriteria) {
        Set<String> filePaths = listFiles(searchCriteria.getPath());
        return movieInfoControl.loadMediaFileList(searchCriteria, filePaths);
    }

    public MediaFile loadSingleMovie(String path) {
        return movieInfoControl.loadSingleMovie(path);
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
