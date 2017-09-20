package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Component
public class FileListProvider implements FileRepository, DirectoryMonitorObserver {

    private final Logger logger = Logger.getLogger(FileListProvider.class.getName());
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime mostRecentUpdate = LocalDateTime.now();

    @CacheEvict(value = "files", allEntries = true)
    public void directoryModified(WatchEvent event, Path absolutePath){
        logger.info("Detected " + event.kind().name() + " " + absolutePath.toString());
        logger.info("Purging cache");
        mostRecentUpdate = LocalDateTime.now();
    }

    @Cacheable(value = "files")
    public File[] listFiles(String directoryPath) {
        logger.info("Listing files at - " + directoryPath);
        File[] files = new File(directoryPath).listFiles();
        if(files == null || files.length == 0)
            files = new File[0];

        return files;
    }

    boolean hasUpdates(String date){
        LocalDateTime dateTime = LocalDateTime.parse(date, dateTimeFormatter);
        return dateTime.isBefore(mostRecentUpdate);
    }
}
