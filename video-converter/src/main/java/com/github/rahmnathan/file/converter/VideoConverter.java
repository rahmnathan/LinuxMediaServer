package com.github.rahmnathan.file.converter;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

public class VideoConverter implements Runnable {
    private final Logger logger = Logger.getLogger(VideoConverter.class.getName());
    private final String ffmpegLocation;
    private final String ffprobeLocation;
    private final File videoFile;
    private volatile Set<String> convertedFiles;

    public VideoConverter(File videoFile, Set<String> convertedFiles, String ffmpegLocation, String ffprobeLocation) {
        this.videoFile = videoFile;
        this.convertedFiles = convertedFiles;
        this.ffprobeLocation = ffprobeLocation;
        this.ffmpegLocation = ffmpegLocation;
    }

    @Override
    public void run() {
        if(ffmpegLocation == null || ffprobeLocation == null || videoFile == null) {
            logger.info("Skipping video conversion due to null inputs");
            return;
        }

        String originalPath = videoFile.getAbsolutePath();
        String newPath = originalPath.substring(0, originalPath.length() - 3) + "mp4";
        logger.info("Encoding " + originalPath + " to " + newPath);
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
                    double duration = in.getFormat().duration;
                    double percentage = progress.out_time_ms / duration;
                    logger.info(originalPath + " Encoding progress -> " + percentage);
                });

            job.run();

            while (true){
                try {
                    Thread.sleep(10000);
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