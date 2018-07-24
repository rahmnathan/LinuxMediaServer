package com.github.rahmnathan.localmovies.service.filesystem;

import com.github.rahmnathan.localmovies.persistence.data.MediaFile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.ManagedBean;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ManagedBean
public class FileListProvider {
    private final Logger logger = LoggerFactory.getLogger(FileListProvider.class.getName());
    private final LoadingCache<String, Set<String>> files;
    private final String[] mediaPaths;

    public FileListProvider(@Value("${media.path}") String[] mediaPaths) {
        this.mediaPaths = mediaPaths;
        this.files  = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new CacheLoader<String, Set<String>>() {
                    @Override
                    public Set<String> load(String path) {
                        return listFilesNoCache(path);
                    }
                });
    }

    public Set<String> listFiles(String path){
        try {
            return files.get(path);
        } catch (ExecutionException e){
            logger.error("Failure loading file list from cache", e);
            return new HashSet<>();
        }
    }

    private Set<String> listFilesNoCache(String path) {
        logger.info("Listing files at - {}", path);

        Set<String> filePaths = new HashSet<>();
        Arrays.stream(mediaPaths).forEach(mediaPath -> {
                    Optional<File[]> fileArray = Optional.ofNullable(new File(mediaPath + path).listFiles());

                    fileArray.ifPresent(files -> {
                        Set<String> tempFileSet = Arrays.stream(files)
                                .map(file -> file.getAbsolutePath().substring(mediaPath.length()))
                                .collect(Collectors.toSet());

                        filePaths.addAll(tempFileSet);
                    });
                });

        return filePaths;
    }

    public void addFile(String relativePath){
        try {
            Set<String> fileSet = files.get(upOneDir(relativePath));
            fileSet.add(relativePath);
        } catch (ExecutionException e){
            logger.error("Error adding media file to cache", e);
        }
    }

    public void removeFile(String relativePath){
        try {
            Set<String> fileSet = files.get(upOneDir(relativePath));
            fileSet.remove(relativePath);
        } catch (ExecutionException e){
            logger.error("Error adding media file to cache", e);
        }
    }

    private String upOneDir(String path){
        String[] dirs = path.split(File.separator);
        return Arrays.stream(dirs)
                .limit(dirs.length - 1)
                .collect(Collectors.joining(File.separator));
    }
}
