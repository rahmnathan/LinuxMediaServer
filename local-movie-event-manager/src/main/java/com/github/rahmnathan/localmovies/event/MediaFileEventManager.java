package com.github.rahmnathan.localmovies.event;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.pushnotification.control.MoviePushNotificationHandler;
import com.github.rahmnathan.localmovies.service.control.MovieInfoProvider;
import com.github.rahmnathan.video.cast.handbrake.control.VideoController;
import com.github.rahmnathan.video.cast.handbrake.data.SimpleConversionJob;
import net.bramp.ffmpeg.FFprobe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
    private final MoviePushNotificationHandler notificationHandler;
    private final MediaEventRepository eventRepository;
    private final MovieInfoProvider movieInfoProvider;
    private FFprobe ffprobe;

    public MediaFileEventManager(@Value("${ffprobe.location:/usr/bin/ffprobe}") String ffprobeLocation, MovieInfoProvider movieInfoProvider,
                                 MediaEventRepository eventRepository, MoviePushNotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
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
        MDC.put("Path", absolutePath.toString());
        logger.info("Detected movie event.");
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

    private void addEvent(WatchEvent watchEvent, String resultFilePath){
        String relativePath = resultFilePath.split("/LocalMedia/")[1];
        MediaFile mediaFile = movieInfoProvider.loadMediaInfo(relativePath);
        if(watchEvent.kind() == StandardWatchEventKinds.ENTRY_DELETE){
            mediaFile = MediaFile.Builder.copyWithNoImage(mediaFile);
        }

        logger.info("Adding event to repository.");
        MediaFileEvent event = new MediaFileEvent(MovieEvent.valueOf(watchEvent.kind().name()).getMovieEventString(), mediaFile, resultFilePath.split("/LocalMedia/")[1]);
        mediaFileEvents.add(event);
        CompletableFuture.runAsync(() -> eventRepository.save(event));

        logger.info("Sending push notification.");
        notificationHandler.sendPushNotifications(mediaFile.getMovie().getTitle());
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

        logger.info("Launching video converter.");
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
