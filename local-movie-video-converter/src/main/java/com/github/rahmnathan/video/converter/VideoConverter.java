package com.github.rahmnathan.video.converter;

import com.github.rahmnathan.directorymonitor.DirectoryMonitorObserver;
import com.github.rahmnathan.media.codec.AudioCodec;
import com.github.rahmnathan.media.codec.ContainerFormat;
import com.github.rahmnathan.media.codec.VideoCodec;
import com.github.rahmnathan.media.converter.VideoController;
import com.github.rahmnathan.media.data.ConversionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class VideoConverter implements DirectoryMonitorObserver {
    @Value("${ffmpeg.location}")
    private String ffmpegLocation;
    @Value("${ffprobe.location}")
    private String ffprobeLocation;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void directoryModified(WatchEvent event, Path absolutePath) {
        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            String newFilePath = absolutePath.toString().substring(0, absolutePath.toString().lastIndexOf('.')) + ".mp4";

            ConversionJob conversionJob = ConversionJob.Builder.newInstance()
                    .setAudioCodec(AudioCodec.AAC)
                    .setVideoCodec(VideoCodec.H264)
                    .setContainerFormat(ContainerFormat.MP4)
                    .setInputFile(absolutePath.toFile())
                    .setOutputFile(new File(newFilePath))
                    .build();

            executorService.submit(new VideoController(conversionJob, ffmpegLocation, ffprobeLocation));
        }
    }
}
