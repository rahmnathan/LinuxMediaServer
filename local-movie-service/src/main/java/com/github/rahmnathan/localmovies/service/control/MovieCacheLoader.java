package com.github.rahmnathan.localmovies.service.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.persistence.MovieRepository;
import com.github.rahmnathan.localmovies.service.utils.PathUtils;
import com.github.rahmnathan.movie.api.MovieProvider;
import com.github.rahmnathan.movie.data.Movie;
import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.io.File;
import java.util.Optional;

@ManagedBean
public class MovieCacheLoader extends CacheLoader<String, MediaFile> {
    private final Logger logger = LoggerFactory.getLogger(MovieCacheLoader.class.getName());
    private final MovieProvider movieProvider;
    private final MovieRepository repository;

    public MovieCacheLoader(MovieProvider movieProvider, MovieRepository repository) {
        this.movieProvider = movieProvider;
        this.repository = repository;
    }

    @Override
    public MediaFile load(String path) {
        Optional<MediaFile> mediaFile = loadMediaInfoFromDatabase(path);

        return mediaFile.orElseGet(() -> {
            if (PathUtils.isTopLevel(path)) {
                return loadMediaInfoFromProvider(path);
            } else {
                return loadSeriesParentInfo(path);
            }
        });
    }

    private Optional<MediaFile> loadMediaInfoFromDatabase(String path){
        logger.info("Getting from database - {}", path);
        return repository.findById(path);
    }

    private MediaFile loadMediaInfoFromProvider(String path) {
        logger.info("Loading MediaFile from provider - {}", path);
        String fileName = new File(path).getName();
        String title = PathUtils.getTitle(fileName);

        Movie movieInfo = movieProvider.loadMovieInfo(title);
        MediaFile mediaFile = MediaFile.Builder.newInstance()
                .setFileName(fileName)
                .setMovie(movieInfo)
                .setPath(path)
                .setViews(0)
                .build();

        repository.save(mediaFile);
        return mediaFile;
    }

    private MediaFile loadSeriesParentInfo(String path) {
        logger.info("Getting info from parent - {}", path);

        String filename = new File(path).getName();
        File file = PathUtils.getParentFile(path);
        logger.info("{} - Parent resolved to: {}", path, file.getPath());

        MediaFile parentInfo = load(file.getPath());
        return MediaFile.Builder.copyWithNewTitle(parentInfo, filename, PathUtils.getTitle(filename));
    }
}
