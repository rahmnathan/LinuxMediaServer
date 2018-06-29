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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class MediaFileEventManager implements DirectoryMonitorObserver {
    private final Logger logger = LoggerFactory.getLogger(MediaFileEventManager.class);
    private volatile Set<String> activeConversions = ConcurrentHashMap.newKeySet();
    private final VideoController videoController = new VideoController();
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
        String relativePath = absolutePath.toString().split("/LocalMedia/")[1];
        MDC.put("Path", relativePath);
        logger.info("Detected movie event.");

        String resultFilePath = absolutePath.toString();
        if(!activeConversions.contains(resultFilePath)) {
            MediaFile mediaFile = null;

            if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                if (Files.isRegularFile(absolutePath) && ffprobe != null) {
                    resultFilePath = launchVideoConverter(resultFilePath);
                }

                mediaFile = getMediaFile(event, relativePath);
                notificationHandler.sendPushNotifications(mediaFile.getMovie().getTitle());
            }

            addEvent(event, mediaFile, resultFilePath);
        }
    }

    private MediaFile getMediaFile(WatchEvent watchEvent, String relativePath){
        MediaFile mediaFile = movieInfoProvider.loadMediaInfo(relativePath);
        if(watchEvent.kind() == StandardWatchEventKinds.ENTRY_DELETE){
            return MediaFile.Builder.copyWithNoImage(mediaFile);
        }

        return mediaFile;
    }

    private void addEvent(WatchEvent watchEvent, MediaFile mediaFile, String resultFilePath){
        logger.info("Adding event to repository.");
        MediaFileEvent event = new MediaFileEvent(MovieEvent.valueOf(watchEvent.kind().name()).getMovieEventString(), mediaFile, resultFilePath.split("/LocalMedia/")[1]);
        mediaFileEvents.add(event);
        eventRepository.save(event);
    }

    private String launchVideoConverter(String inputFilePath){
        // I need to find a way to wait until a file is fully written before converting it
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e){
            logger.error("Failed sleep", e);
        }

        String resultFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf('.')) + ".mp4";
        SimpleConversionJob conversionJob = new SimpleConversionJob(ffprobe, new File(resultFilePath), new File(inputFilePath));

        logger.info("Launching video converter.");
        return videoController.convertVideo(conversionJob, activeConversions);
    }

    public List<MediaFileEvent> getMediaFileEvents(LocalDateTime lastQueryTime){
        return mediaFileEvents.stream()
                .sorted(Comparator.comparing(MediaFileEvent::getTimestamp))
                .filter(mediaFileEvent -> mediaFileEvent.getTimestamp().isAfter(lastQueryTime))
                .collect(Collectors.toList());
    }
}
