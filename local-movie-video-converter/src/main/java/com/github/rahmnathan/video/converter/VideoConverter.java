package com.github.rahmnathan.video.converter;

import com.github.rahmnathan.video.job.SimpleConversionJob;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.logging.Logger;

public class VideoConverter {
    private final Logger logger = Logger.getLogger(VideoConverter.class.getName());

    public void convertMedia(SimpleConversionJob simpleConversionJob) {
        validateParams(simpleConversionJob);

        String existingFilePath = simpleConversionJob.getInputFile().getAbsolutePath();
        String newFilePath = simpleConversionJob.getOutputFile().getAbsolutePath();

        logger.info("Encoding " + existingFilePath + " to " + newFilePath);

        FFmpegOutputBuilder outputBuilder = new FFmpegBuilder()
                .setInput(existingFilePath)
                .overrideOutputFiles(true)
                .addOutput(newFilePath);

        if (simpleConversionJob.getAudioCodec() != null)
            outputBuilder.setAudioCodec(simpleConversionJob.getAudioCodec().getEncoder());
        if (simpleConversionJob.getVideoCodec() != null)
            outputBuilder.setVideoCodec(simpleConversionJob.getVideoCodec().getEncoder());

        try {
            FFmpegProbeResult in = simpleConversionJob.getFfprobe().probe(existingFilePath);
            FFmpegExecutor executor = new FFmpegExecutor(simpleConversionJob.getFfmpeg(), simpleConversionJob.getFfprobe());

            FFmpegJob job = executor.createJob(outputBuilder.done(), progress -> {
                double duration = in.getFormat().duration;
                int percentage =
                        Double.valueOf((progress.out_time_ms / duration) / 10000).intValue();
                logger.info(existingFilePath + " Encoding progress -> " + percentage + "%");
            });

            job.run();

            while (true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    logger.severe(e.toString());
                    break;
                }
                if (job.getState() == FFmpegJob.State.FAILED) {
                    logger.info("Encoding Failed");
                    break;
                } else if (job.getState() == FFmpegJob.State.FINISHED) {
                    logger.info("Encoding Finished");
                    simpleConversionJob.getInputFile().delete();
                    break;
                }
            }
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    private void validateParams(SimpleConversionJob simpleConversionJob) {
        Validate.notNull(simpleConversionJob, "Conversion job is null");
        Validate.notNull(simpleConversionJob.getFfmpeg(), "FFmpeg is null");
        Validate.notNull(simpleConversionJob.getFfprobe(), "FFprobe is null");
        Validate.notNull(simpleConversionJob.getInputFile(), "Input file is null");
        Validate.notNull(simpleConversionJob.getOutputFile(), "Output file is null");
    }
}
