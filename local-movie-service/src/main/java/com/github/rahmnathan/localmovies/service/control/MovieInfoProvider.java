package com.github.rahmnathan.localmovies.service.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.util.concurrent.ExecutionException;

@ManagedBean
public class MovieInfoProvider {
    private final Logger logger = LoggerFactory.getLogger(MovieInfoProvider.class.getName());
    private LoadingCache<String, MediaFile> movieInfoCache;

    public MovieInfoProvider(MovieCacheLoader cacheLoader){
        movieInfoCache = CacheBuilder.newBuilder()
                .maximumSize(500)
                .build(cacheLoader);
    }

    MediaFile loadMediaInfo(String path){
        try {
            return movieInfoCache.get(path);
        } catch (ExecutionException e){
            logger.error("Failed to load media info from cache", e);
            return MediaFile.Builder.newInstance().build();
        }
    }
}