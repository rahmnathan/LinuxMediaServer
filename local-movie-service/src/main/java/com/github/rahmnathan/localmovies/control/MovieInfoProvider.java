package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.omdb.info.provider.OmdbMovieInfoProvider;
import com.github.rahmnathan.localmovies.persistence.MovieInfoRepository;
import com.github.rahmnathan.movie.info.api.IMovieInfoProvider;
import com.github.rahmnathan.movie.info.data.MovieInfo;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Component
public class MovieInfoProvider {
    @Value("${omdb.api.key}")
    private String apiKey;
    private final Logger logger = Logger.getLogger(MovieInfoProvider.class.getName());
    private IMovieInfoProvider movieInfoProvider;
    private final MovieInfoRepository repository;
    private final LoadingCache<String, MediaFile> movieInfoCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .build(
                    new CacheLoader<String, MediaFile>() {
                        @Override
                        public MediaFile load(String currentPath) {
                            if (repository.exists(currentPath)) {
                                return loadMovieInfoFromDatabase(currentPath);
                            } else if (isViewingTopLevel(currentPath)) {
                                return loadMovieInfoFromProvider(currentPath);
                            } else {
                                return loadSeriesParentInfo(currentPath);
                            }
                        }
                    });

    @Autowired
    public MovieInfoProvider(MovieInfoRepository repository){
        this.repository = repository;
    }

    @PostConstruct
    public void initialize(){
        movieInfoProvider = new OmdbMovieInfoProvider(apiKey);
    }

    public MediaFile loadMovieInfoFromCache(String path){
        try {
            return movieInfoCache.get(path);
        } catch (ExecutionException e){
            logger.severe(e.toString());
            return MediaFile.Builder.newInstance().build();
        }
    }

    private MediaFile loadMovieInfoFromDatabase(String path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path);
    }

    private MediaFile loadMovieInfoFromProvider(String path) {
        logger.info("Loading MediaFile from provider - " + path);
        String[] pathArray = path.split(File.separator);
        String title = pathArray[pathArray.length - 1];
        MovieInfo movieInfo = movieInfoProvider.loadMovieInfo(title);
        MediaFile mediaFile = MediaFile.Builder.newInstance()
                .setMovieInfo(movieInfo)
                .setPath(path)
                .setViews(0)
                .build();

        repository.save(mediaFile);
        return mediaFile;
    }

    private MediaFile loadSeriesParentInfo(String path) {
        logger.info("Getting info from parent - " + path);
        String[] pathArray = path.split(File.separator);
        int depth = pathArray.length > 2 ? pathArray.length - 2 : 0;

        StringBuilder sb = new StringBuilder();
        Arrays.stream(pathArray)
                .limit(pathArray.length - depth)
                .forEachOrdered(directory-> sb.append(directory).append(File.separator));

        MediaFile mediaFile = loadMovieInfoFromCache(sb.toString().substring(0, sb.length() - 1));
        return MediaFile.Builder.copyWithNewTitle(mediaFile, pathArray[pathArray.length - 1]);
    }

    private boolean isViewingTopLevel(String currentPath){
        return currentPath.split(File.separator).length == 2;
    }
}