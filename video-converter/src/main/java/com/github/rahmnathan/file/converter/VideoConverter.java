package com.github.rahmnathan.file.converter;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoConverter implements Runnable {
    @Value("${ffmpeg.location}")
    private String ffmpegLocation;
    @Value("${ffprobe.location}")
    private String ffprobeLocation;
    private final File videoFile;
    private volatile Set<String> convertedFiles;
    private final Logger logger = Logger.getLogger(VideoConverter.class.getName());

    public VideoConverter(File videoFile, Set<String> convertedFiles) {
        this.videoFile = videoFile;
        this.convertedFiles = convertedFiles;
    }

    @Override
    public void run() {
        if(ffmpegLocation == null || ffprobeLocation == null || ffmpegLocation.equals("") || ffprobeLocation.equals(""))
            return;

        String originalPath = videoFile.getAbsolutePath();
        String newPath = originalPath.substring(0, originalPath.length() - 3) + "mp4";
        convertedFiles.add(newPath);
        
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(originalPath)
                .overrideOutputFiles(true)
                .addOutput(newPath)
                .setFormat("mp4")
                .disableSubtitle()
                .setAudioCodec("aac")
                .setVideoCodec("libx264")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();

        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegLocation);
            FFprobe ffprobe = new FFprobe(ffprobeLocation);

            FFmpegProbeResult in = ffprobe.probe(originalPath);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            FFmpegJob job = executor.createJob(builder, progress -> {
                    final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
                    double percentage = progress.out_time_ms / duration_ns;
                    logger.info(originalPath + " Encoding progress -> " + percentage + "%");
                });

            job.run();

            while (true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e){
                    logger.severe(e.toString());
                    break;
                }
                if(job.getState() == FFmpegJob.State.FAILED) {
                    break;
                } else if(job.getState() == FFmpegJob.State.FINISHED) {
                    videoFile.delete();
                    break;
                }
            }
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }
}