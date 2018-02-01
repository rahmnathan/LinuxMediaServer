package com.github.rahmnathan.localmovies.service.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.persistence.MovieInfoRepository;
import com.github.rahmnathan.localmovies.service.utils.PathUtils;
import com.github.rahmnathan.movie.info.api.IMovieInfoProvider;
import com.github.rahmnathan.movie.info.data.MovieInfo;
import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.io.File;

@ManagedBean
public class MovieCacheLoader extends CacheLoader<String, MediaFile> {
    private final Logger logger = LoggerFactory.getLogger(MovieCacheLoader.class.getName());
    private final IMovieInfoProvider movieInfoProvider;
    private final MovieInfoRepository repository;

    public MovieCacheLoader(IMovieInfoProvider movieInfoProvider, MovieInfoRepository repository) {
        this.movieInfoProvider = movieInfoProvider;
        this.repository = repository;
    }

    @Override
    public MediaFile load(String path) {
        if (repository.exists(path)) {
            return loadMediaInfoFromDatabase(path);
        } else if (PathUtils.isTopLevel(path)) {
            return loadMediaInfoFromProvider(path);
        } else {
            return loadSeriesParentInfo(path);
        }
    }

    private MediaFile loadMediaInfoFromDatabase(String path){
        logger.info("Getting from database - {}", path);
        return repository.findOne(path);
    }

    private MediaFile loadMediaInfoFromProvider(String path) {
        logger.info("Loading MediaFile from provider - {}", path);
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
        logger.info("Getting info from parent - {}", path);

        String filename = new File(path).getName();
        File file = PathUtils.getParentFile(path);
        logger.info("{} - Parent resolved to: {}", path, file.getPath());

        MediaFile parentInfo = load(file.getPath());
        return MediaFile.Builder.copyWithNewTitle(parentInfo, filename, PathUtils.getTitle(filename));
    }
}
