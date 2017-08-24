package com.github.rahmnathan.media.converter;

import com.github.rahmnathan.media.data.ConversionJob;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

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

        FFmpegOutputBuilder outputBuilder = new FFmpegOutputBuilder()
                .setUri(URI.create(newFilePath))
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL);

        if(conversionJob.getAudioCodec() != null)
            outputBuilder.setAudioCodec(conversionJob.getAudioCodec().getFfmpegFormat());
        if(conversionJob.getVideoCodec() != null)
            outputBuilder.setVideoCodec(conversionJob.getVideoCodec().getFfmpegFormat());
        if(conversionJob.getContainerFormat() != null)
            outputBuilder.setFormat(conversionJob.getContainerFormat().getFfmpegFormat());

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(existingFilePath)
                .overrideOutputFiles(true)
                .addOutput(outputBuilder);

        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegLocation);
            FFprobe ffprobe = new FFprobe(ffprobeLocation);

            FFmpegProbeResult in = ffprobe.probe(existingFilePath);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            FFmpegJob job = executor.createJob(builder, progress -> {
                    double duration = in.getFormat().duration;
                    double percentage = progress.out_time_ms / duration;
                    logger.info(existingFilePath + " Encoding progress -> " + percentage);
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
                    conversionJob.getInputFile().delete();
                    break;
                }
            }
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }
}