package com.github.rahmnathan.localmovie.service.boundary;

import com.github.rahmnathan.localmovie.domain.MediaFile;
import com.github.rahmnathan.localmovie.domain.MovieSearchCriteria;
import com.github.rahmnathan.localmovie.service.control.MediaCacheLoader;
import com.github.rahmnathan.localmovie.domain.MovieClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.github.rahmnathan.localmovie.service.control.MediaFileUtils.paginateMediaFiles;
import static com.github.rahmnathan.localmovie.service.control.MediaFileUtils.removePosterImages;
import static com.github.rahmnathan.localmovie.service.control.MediaFileUtils.sortMediaFiles;

@ManagedBean
public class MediaMetadataService {
    private final Logger logger = LoggerFactory.getLogger(MediaMetadataService.class);
    private final LoadingCache<String, MediaFile> movieInfoCache;
    private final FileListProvider fileListProvider;

    public MediaMetadataService(MediaCacheLoader cacheLoader, FileListProvider fileListProvider) {
        this.fileListProvider = fileListProvider;
       this.movieInfoCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(cacheLoader);
    }

    public int loadMediaListLength(String directoryPath){
        return fileListProvider.listFiles(directoryPath).size();
    }

    public MediaFile loadSingleMediaFile(String filePath) {
        return getMediaMetadata(filePath);
    }

    public List<MediaFile> loadMediaFileList(MovieSearchCriteria searchCriteria) {
        Set<String> files = fileListProvider.listFiles(searchCriteria.getPath());
        List<MediaFile> movies = loadMedia(files);

        if (searchCriteria.getClient() == MovieClient.WEBAPP) {
            movies = removePosterImages(movies);
        }

        movies = sortMediaFiles(searchCriteria, movies);
        return paginateMediaFiles(movies, searchCriteria);
    }

    private List<MediaFile> loadMedia(Set<String> relativePaths){
        return relativePaths.parallelStream()
                .sorted()
                .map(this::getMediaMetadata)
                .collect(Collectors.toList());
    }

    public MediaFile getMediaMetadata(String path){
        try {
            return movieInfoCache.get(path);
        } catch (ExecutionException e){
            logger.error("Failed to load media info from cache", e);
            return MediaFile.Builder.newInstance().build();
        }
    }

    public void addMediaMetadata(String path){
        fileListProvider.addFile(path);
    }

    public void deleteMediaMetadata(String path){
        fileListProvider.removeFile(path);
        movieInfoCache.invalidate(path);
    }
}
