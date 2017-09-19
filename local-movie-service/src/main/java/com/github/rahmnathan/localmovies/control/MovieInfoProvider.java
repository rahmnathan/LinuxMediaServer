package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.data.MovieInfoWithContext;
import com.github.rahmnathan.localmovies.movieinfoapi.IMovieInfoProvider;
import com.github.rahmnathan.localmovies.persistence.MovieInfoRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Component
public class MovieInfoProvider {

    private final MovieInfoRepository repository;
    private final IMovieInfoProvider movieInfoProvider;
    private final Logger logger = Logger.getLogger(MovieInfoProvider.class.getName());
    private final LoadingCache<String, MovieInfoWithContext> movieInfoCache =
            CacheBuilder.newBuilder()
                    .maximumSize(500)
                    .build(
                            new CacheLoader<String, MovieInfoWithContext>() {
                                @Override
                                public MovieInfoWithContext load(String currentPath) {
                                    if(repository.exists(currentPath)){
                                        return loadMovieInfoFromDatabase(currentPath);
                                    } else if (isViewingTopLevel(currentPath)){
                                        return loadMovieInfoFromProvider(currentPath);
                                    } else {
                                        return loadSeriesParentInfo(currentPath);
                                    }
                                }
                            });

    @Autowired
    public MovieInfoProvider(MovieInfoRepository repository, IMovieInfoProvider movieInfoProvider){
        this.repository = repository;
        this.movieInfoProvider = movieInfoProvider;
    }

    public MovieInfoWithContext loadMovieInfoFromCache(String path){
        try {
            return movieInfoCache.get(path);
        } catch (ExecutionException e){
            logger.severe(e.toString());
            return MovieInfoWithContext.Builder.newInstance().build();
        }
    }

    private MovieInfoWithContext loadMovieInfoFromDatabase(String path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path);
    }

    private MovieInfoWithContext loadMovieInfoFromProvider(String path) {
        logger.info("Loading MovieInfoWithContext from provider - " + path);
        String[] pathArray = path.split(File.separator);
        String title = pathArray[pathArray.length - 1];
        MovieInfoWithContext movieInfoWithContext = movieInfoProvider.loadMovieInfo(title);
        MovieInfoWithContext.Builder.newInstance()

        movieInfoWithContext.setPath(path);
        repository.save(movieInfoWithContext);
        return movieInfoWithContext;
    }

    private MovieInfoWithContext loadSeriesParentInfo(String path) {
        logger.info("Getting info from parent - " + path);
        String[] pathArray = path.split(File.separator);
        int depth = pathArray.length > 2 ? pathArray.length - 2 : 0;

        StringBuilder sb = new StringBuilder();
        Arrays.stream(pathArray)
                .limit(pathArray.length - depth)
                .forEachOrdered(directory-> sb.append(directory).append(File.separator));

        MovieInfoWithContext movieInfoWithContext = loadMovieInfoFromCache(sb.toString().substring(0, sb.length() - 1));
        return MovieInfoWithContext.Builder.copyWithNewTitle(movieInfoWithContext, pathArray[pathArray.length - 1]);
    }

    private boolean isViewingTopLevel(String currentPath){
        return currentPath.split(File.separator).length == 2;
    }
}