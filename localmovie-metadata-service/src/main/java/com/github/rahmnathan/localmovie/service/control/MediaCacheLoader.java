package com.github.rahmnathan.localmovie.service.control;

import com.github.rahmnathan.localmovie.domain.MediaFile;
import com.github.rahmnathan.omdb.boundary.OmdbMovieProvider;
import com.github.rahmnathan.omdb.exception.MovieProviderException;
import com.github.rahmnathan.localmovie.service.persistence.MovieRepository;
import com.google.common.cache.CacheLoader;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.io.File;
import java.util.Optional;

@ManagedBean
public class MediaCacheLoader extends CacheLoader<String, MediaFile> {
    private final Logger logger = LoggerFactory.getLogger(MediaCacheLoader.class.getName());
    private static final Timer MOVIE_PROVIDER_TIMER = Metrics.timer("localmovies.omdb.timer");
    private final OmdbMovieProvider movieProvider;
    private final MovieRepository repository;

    public MediaCacheLoader(MovieRepository repository, OmdbMovieProvider movieProvider) {
        this.movieProvider = movieProvider;
        this.repository = repository;
    }

    @Override
    public MediaFile load(String path) {
        Optional<MediaFile> mediaFile = repository.findById(path);
        if (mediaFile.isPresent()) {
            logger.info("Getting from database - {}", path);
            return mediaFile.get();
        } else if (PathUtils.isTopLevel(path)) {
            return loadMediaInfoFromProvider(path);
        } else {
            return loadSeriesParentInfo(path);
        }
    }

    private MediaFile loadMediaInfoFromProvider(String path) {
        logger.info("Loading MediaFile from provider - {}", path);
        String fileName = new File(path).getName();
        String title = PathUtils.getTitle(fileName);

        MediaFile.Builder builder = MediaFile.Builder.newInstance()
                .setFileName(fileName)
                .setPath(path)
                .setViews(0);

        MOVIE_PROVIDER_TIMER.record(() -> {
            try {
                builder.setMovie(movieProvider.getMovie(title));
            } catch (MovieProviderException e){
                logger.error("Error getting movie from provider", e);
            }
        });

        MediaFile mediaFile = builder.build();
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
