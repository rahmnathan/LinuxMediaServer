package com.github.rahmnathan.localmovies.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.github.rahmnathan.localmovies.movieinfoapi.IMovieInfoProvider;
import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfoRepository;
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
    private final LoadingCache<String, MovieInfo> movieInfoCache =
            CacheBuilder.newBuilder()
                    .maximumSize(500)
                    .build(
                            new CacheLoader<String, MovieInfo>() {
                                @Override
                                public MovieInfo load(String currentPath) {
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

    public MovieInfo loadMovieInfoFromCache(String path){
        try {
            return movieInfoCache.get(path);
        } catch (ExecutionException e){
            logger.severe(e.toString());
            return MovieInfo.Builder.newInstance().build();
        }
    }

    private MovieInfo loadMovieInfoFromDatabase(String path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path);
    }

    private MovieInfo loadMovieInfoFromProvider(String path) {
        logger.info("Loading MovieInfo from provider - " + path);
        String[] pathArray = path.split(File.separator);
        String title = pathArray[pathArray.length - 1];
        MovieInfo movieInfo = movieInfoProvider.loadMovieInfo(title);
        movieInfo.setPath(path);
        repository.save(movieInfo);
        return movieInfo;
    }

    private MovieInfo loadSeriesParentInfo(String path) {
        logger.info("Getting info from parent - " + path);
        String[] pathArray = path.split(File.separator);
        int depth = pathArray.length > 2 ? pathArray.length - 2 : 0;

        StringBuilder sb = new StringBuilder();
        Arrays.stream(pathArray)
                .limit(pathArray.length - depth)
                .forEachOrdered(directory-> sb.append(directory).append(File.separator));

        MovieInfo movieInfo = loadMovieInfoFromCache(sb.toString().substring(0, sb.length() - 1));
        return MovieInfo.Builder.copyWithNewTitle(movieInfo, pathArray[pathArray.length - 1]);
    }

    private boolean isViewingTopLevel(String currentPath){
        return currentPath.split(File.separator).length == 2;
    }
}