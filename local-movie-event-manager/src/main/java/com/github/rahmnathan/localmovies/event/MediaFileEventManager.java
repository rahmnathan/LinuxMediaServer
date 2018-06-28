package com.github.rahmnathan.localmovies.event;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.service.control.MovieInfoProvider;
import com.github.rahmnathan.video.cast.handbrake.control.VideoController;
import com.github.rahmnathan.video.cast.handbrake.data.SimpleConversionJob;
import net.bramp.ffmpeg.FFprobe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class MediaFileEventManager implements DirectoryMonitorObserver {
    private final Logger logger = LoggerFactory.getLogger(MediaFileEventManager.class);
    private volatile Set<String> activeConversions = ConcurrentHashMap.newKeySet();
    private final List<MediaFileEvent> mediaFileEvents = new ArrayList<>();
    private final MediaEventRepository eventRepository;
    private final MovieInfoProvider movieInfoProvider;
    private FFprobe ffprobe;

    public MediaFileEventManager(@Value("${ffprobe.location:/usr/bin/ffprobe}") String ffprobeLocation, MovieInfoProvider movieInfoProvider,
                                 MediaEventRepository eventRepository) {
        this.movieInfoProvider = movieInfoProvider;
        this.eventRepository = eventRepository;

        eventRepository.findAll().forEach(mediaFileEvents::add);

        try {
            this.ffprobe = new FFprobe(ffprobeLocation);
        } catch (IOException e){
            logger.error("Failed to instantiate MediaFileEventManager", e);
        }
    }

    @Override
    public void directoryModified(WatchEvent event, Path absolutePath) {
        String resultFilePath = absolutePath.toString();

        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                && !activeConversions.contains(resultFilePath)
                && Files.isRegularFile(absolutePath)
                && ffprobe != null) {

            launchVideoConverter(resultFilePath, event);
        } else {
            addEvent(event, resultFilePath);
        }
    }

    private void addEvent(WatchEvent event, String resultFilePath){
        MediaFile newMediaFileEvent = movieInfoProvider.loadMediaInfo(resultFilePath.split("/LocalMedia/")[1]);
        if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
            newMediaFileEvent = MediaFile.Builder.copyWithNoImage(newMediaFileEvent);
        }

        MediaFileEvent event1 = new MediaFileEvent(MovieEvent.valueOf(event.kind().name()).getMovieEventString(), newMediaFileEvent, resultFilePath.split("/LocalMedia/")[1]);
        mediaFileEvents.add(event1);
        eventRepository.save(event1);
    }

    private void launchVideoConverter(String inputFilePath, WatchEvent event){
        // I need to find a way to wait until a file is fully written before converting it
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e){
            logger.error("Failed sleep", e);
        }

        String resultFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf('.')) + ".mp4";

        SimpleConversionJob conversionJob = new SimpleConversionJob(ffprobe, new File(resultFilePath), new File(inputFilePath));
        CompletableFuture.supplyAsync(new VideoController(conversionJob, activeConversions))
                .thenAccept(path -> addEvent(event, path));
    }

    public List<MediaFileEvent> getMediaFileEvents(LocalDateTime lastQueryTime){
        return mediaFileEvents.stream()
                .sorted(Comparator.comparing(MediaFileEvent::getTimestamp))
                .filter(mediaFileEvent -> mediaFileEvent.getTimestamp().isAfter(lastQueryTime))
                .collect(Collectors.toList());
    }
}
