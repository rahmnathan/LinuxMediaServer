package com.github.rahmnathan.localmovies.service.filesystem;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.annotation.ManagedBean;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ManagedBean
public class FileListProvider implements FileRepository, DirectoryMonitorObserver {

    private final String[] mediaPaths;
    private final Logger logger = LoggerFactory.getLogger(FileListProvider.class.getName());

    public FileListProvider(@Value("${media.path}") String[] mediaPaths) {
        this.mediaPaths = mediaPaths;
    }

    @CacheEvict(value = "files", allEntries = true)
    public void directoryModified(WatchEvent event, Path absolutePath) {
        logger.info("Detected {} {}", event.kind().name(), absolutePath.toString());
        logger.info("Purging cache");
    }

    @Cacheable(value = "files")
    public Set<String> listFiles(String path) {
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
}
