package com.github.localmovies.event;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.service.control.MovieInfoProvider;
import com.github.rahmnathan.video.cast.handbrake.control.VideoController;
import com.github.rahmnathan.video.cast.handbrake.data.SimpleConversionJob;
import net.bramp.ffmpeg.FFprobe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MediaFileEventManager implements DirectoryMonitorObserver {
    private final Logger logger = LoggerFactory.getLogger(MediaFileEventManager.class);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile Set<String> activeConversions = ConcurrentHashMap.newKeySet();
    private final List<MediaFileEvent> mediaFileEvents = new ArrayList<>();
    private final MovieInfoProvider movieInfoProvider;
    private FFprobe ffprobe;

    public MediaFileEventManager(@Value("${ffprobe.location:/usr/bin/ffprobe}") String ffprobeLocation, MovieInfoProvider movieInfoProvider) {
        this.movieInfoProvider = movieInfoProvider;

        try {
            this.ffprobe = new FFprobe(ffprobeLocation);
        } catch (IOException e){
            logger.error("Failed to instantiate VideoConversionMonitor", e);
        }
    }

    @Override
    public void directoryModified(WatchEvent event, Path absolutePath) {
        if (ffprobe == null)
            return;

        String resultFilePath = absolutePath.toString();

        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isRegularFile(absolutePath) &&
                !activeConversions.contains(absolutePath.toString())) {

            // I need to find a way to wait until a file is fully written before converting it
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e){
                logger.error("Failed sleep", e);
            }

            String newFilePath = absolutePath.toString().substring(0, absolutePath.toString().lastIndexOf('.')) + ".mp4";
            resultFilePath = newFilePath;

            SimpleConversionJob conversionJob = new SimpleConversionJob(ffprobe, new File(newFilePath), absolutePath.toFile());

            executorService.submit(new VideoController(conversionJob, activeConversions));
        }

        MediaFile newMediaFileEvent = movieInfoProvider.loadMediaInfo(resultFilePath.split("/LocalMedia/")[1]);
        mediaFileEvents.add(new MediaFileEvent(event.kind(), newMediaFileEvent));
    }

    public List<MediaFileEvent> getMediaFileEvents(LocalDateTime localDateTime){
        return mediaFileEvents.stream()
                .filter(mediaFileEvent -> mediaFileEvent.getLocalDateTime().isAfter(localDateTime))
                .collect(Collectors.toList());
    }

    public Set<String> getActiveConversions() {
        return activeConversions;
    }
}
