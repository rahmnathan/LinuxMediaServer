package com.github.rahmnathan.video.control;

import com.github.rahmnathan.directorymonitor.DirectoryMonitorObserver;
import com.github.rahmnathan.video.codec.AudioCodec;
import com.github.rahmnathan.video.codec.VideoCodec;
import com.github.rahmnathan.video.job.SimpleConversionJob;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Component
public class VideoConversionMonitor implements DirectoryMonitorObserver {
    private final Logger logger = Logger.getLogger(VideoConversionMonitor.class.getName());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Value("${ffmpeg.location}")
    private String ffmpegLocation;
    @Value("${ffprobe.location}")
    private String ffprobeLocation;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;

    @PostConstruct
    private void initialize() {
        try {
            ffmpeg = new FFmpeg(ffmpegLocation);
            ffprobe = new FFprobe(ffprobeLocation);
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    @Override
    public void directoryModified(WatchEvent event, Path absolutePath) {
        if (ffmpeg == null || ffprobe == null)
            return;

        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isRegularFile(absolutePath)) {
            String newFilePath = absolutePath.toString().substring(0, absolutePath.toString().lastIndexOf('.')) + ".mp4";

            SimpleConversionJob conversionJob = SimpleConversionJob.Builder.newInstance()
                    .setFfmpeg(ffmpeg)
                    .setFfprobe(ffprobe)
                    .setAudioCodec(AudioCodec.AAC)
                    .setVideoCodec(VideoCodec.H264)
                    .setInputFile(absolutePath.toFile())
                    .setOutputFile(new File(newFilePath))
                    .build();

            executorService.submit(new VideoController(conversionJob));
        }
    }
}
