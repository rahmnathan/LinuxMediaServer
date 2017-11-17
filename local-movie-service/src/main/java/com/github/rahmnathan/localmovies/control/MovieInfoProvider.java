package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.omdb.info.provider.OmdbMovieInfoProvider;
import com.github.rahmnathan.localmovies.persistence.MovieInfoRepository;
import com.github.rahmnathan.localmovies.utils.PathUtils;
import com.github.rahmnathan.movie.info.api.IMovieInfoProvider;
import com.github.rahmnathan.movie.info.data.MovieInfo;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MovieInfoProvider {
    private final Logger logger = Logger.getLogger(MovieInfoProvider.class.getName());
    private final IMovieInfoProvider movieInfoProvider;
    private final MovieInfoRepository repository;
    private final LoadingCache<String, MediaFile> movieInfoCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .build(
                    new CacheLoader<String, MediaFile>() {
                        @Override
                        public MediaFile load(@Nonnull String path) {
                            if (repository.exists(path)) {
                                return loadMediaInfoFromDatabase(path);
                            } else if (PathUtils.isTopLevel(path)) {
                                return loadMediaInfoFromProvider(path);
                            } else {
                                return loadSeriesParentInfo(path);
                            }
                        }
                    });

    @Autowired
    public MovieInfoProvider(MovieInfoRepository repository, OmdbMovieInfoProvider movieInfoProvider){
        this.repository = repository;
        this.movieInfoProvider = movieInfoProvider;
    }

    MediaFile loadMediaInfo(String path){
        try {
            return movieInfoCache.get(path);
        } catch (ExecutionException e){
            logger.log(Level.SEVERE, "Failed to load media info from cache", e);
            return MediaFile.Builder.newInstance().build();
        }
    }

    private MediaFile loadMediaInfoFromDatabase(String path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path);
    }

    private MediaFile loadMediaInfoFromProvider(String path) {
        logger.info("Loading MediaFile from provider - " + path);
        String fileName = new File(path).getName();
        String title = PathUtils.getTitle(fileName);

        MovieInfo movieInfo = movieInfoProvider.loadMovieInfo(title);
        MediaFile mediaFile = MediaFile.Builder.newInstance()
                .setFileName(fileName)
                .setMovieInfo(movieInfo)
                .setPath(path)
                .setViews(0)
                .build();

        repository.save(mediaFile);
        return mediaFile;
    }

    private MediaFile loadSeriesParentInfo(String path) {
        logger.info("Getting info from parent - " + path);

        String filename = new File(path).getName();
        File file = PathUtils.getParentFile(path);
        logger.info(path + " - Parent resolved to: " + file.getPath());

        MediaFile parentInfo = loadMediaInfo(file.getPath());
        return MediaFile.Builder.copyWithNewTitle(parentInfo, filename, PathUtils.getTitle(filename));
    }
}