package com.github.rahmnathan.file.converter;

import com.github.rahmnathan.directorymonitor.DirectoryMonitorObserver;
import io.humble.video.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Component
public class VideoController implements DirectoryMonitorObserver {
    private final Logger logger = Logger.getLogger(VideoController.class.getName());
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void directoryModified(WatchEvent event, Path absolutePath) {
        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            convertToCastableFormat(absolutePath.toFile());
        }
    }

    public void convertToCastableFormat(File videoFile) {
        try {
            if (!isCorrectFormat(videoFile))
                executor.execute(new VideoConverter(videoFile));
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

    private boolean isCorrectFormat(File videoFile) throws InterruptedException, IOException {
        boolean isH264 = false;
        boolean isAAC = false;
        boolean fileIsBeingModified = true;

        long fileSize = videoFile.length();
        while (fileIsBeingModified) {
            Thread.sleep(1000);
            if (fileSize != videoFile.length()) {
                fileSize = videoFile.length();
            } else {
                fileIsBeingModified = false;
            }
        }

        Demuxer demuxer = Demuxer.make();
        demuxer.open(videoFile.getAbsolutePath(), null, false, true, null, null);
        for (int i = 0; i < demuxer.getNumStreams(); i++) {
            DemuxerStream stream = demuxer.getStream(i);
            Codec.ID codecId = stream.getDecoder().getCodecID();
            if (codecId == Codec.ID.CODEC_ID_H264)
                isH264 = true;
            if (codecId == Codec.ID.CODEC_ID_AAC)
                isAAC = true;
        }
        demuxer.close();
        return isH264 && isAAC;
    }
}