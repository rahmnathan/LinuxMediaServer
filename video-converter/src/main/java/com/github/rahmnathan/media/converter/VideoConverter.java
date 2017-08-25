package com.github.rahmnathan.media.converter;

import com.github.rahmnathan.media.data.ConversionJob;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class VideoConverter implements Runnable {
    private final Logger logger = Logger.getLogger(VideoConverter.class.getName());
    private final ConversionJob conversionJob;
    private final String ffmpegLocation;
    private final String ffprobeLocation;

    public VideoConverter(ConversionJob conversionJob, String ffmpegLocation, String ffprobeLocation) {
        this.conversionJob = conversionJob;
        this.ffprobeLocation = ffprobeLocation;
        this.ffmpegLocation = ffmpegLocation;
    }

    @Override
    public void run() {
        if(ffmpegLocation == null || ffprobeLocation == null || conversionJob == null ||
                conversionJob.getInputFile() == null || conversionJob.getOutputFile() == null) {
            logger.info("Skipping video conversion due to null inputs");
            return;
        }

        String existingFilePath = conversionJob.getInputFile().getAbsolutePath();
        String newFilePath = conversionJob.getOutputFile().getAbsolutePath();

        logger.info("Encoding " + existingFilePath + " to " + newFilePath);

        FFmpegOutputBuilder outputBuilder = new FFmpegBuilder()
                .setInput(existingFilePath)
                .overrideOutputFiles(true)
                .addOutput(newFilePath);

        if(conversionJob.getAudioCodec() != null)
            outputBuilder.setAudioCodec(conversionJob.getAudioCodec().getFfmpegFormat());
        if(conversionJob.getVideoCodec() != null)
            outputBuilder.setVideoCodec(conversionJob.getVideoCodec().getFfmpegFormat());
        if(conversionJob.getContainerFormat() != null)
            outputBuilder.setFormat(conversionJob.getContainerFormat().getFfmpegFormat());

        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegLocation);
            FFprobe ffprobe = new FFprobe(ffprobeLocation);

            FFmpegProbeResult in = ffprobe.probe(existingFilePath);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            FFmpegJob job = executor.createJob(outputBuilder.done(), progress -> {
                    double duration = in.getFormat().duration;
                    int percentage = Double.valueOf((progress.out_time_ms / duration) / 10000).intValue();
                    logger.info(existingFilePath + " Encoding progress -> " + percentage + "%");
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
                    logger.info("Encoding Failed");
                    break;
                } else if(job.getState() == FFmpegJob.State.FINISHED) {
                    logger.info("Encoding Finished");
                    conversionJob.getInputFile().delete();
                    break;
                }
            }
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }
}