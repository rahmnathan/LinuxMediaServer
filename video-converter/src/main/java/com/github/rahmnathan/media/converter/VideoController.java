package com.github.rahmnathan.media.converter;

import com.github.rahmnathan.media.data.ConversionJob;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoController {
    private String ffmpegLocation;
    private String ffprobeLocation;
    private final Logger logger = Logger.getLogger(VideoController.class.getName());
    private final Executor executor = Executors.newSingleThreadExecutor();

    public VideoController (String ffmpegLocation, String ffprobeLocation){
        this.ffmpegLocation = ffmpegLocation;
        this.ffprobeLocation = ffprobeLocation;
    }

    public void convertToCastableFormat(ConversionJob conversionJob) {
        if (!isCorrectFormat(conversionJob)) {
            executor.execute(new VideoConverter(conversionJob, ffmpegLocation, ffprobeLocation));
        }
    }

    public boolean isCorrectFormat(ConversionJob conversionJob) {
        if(ffprobeLocation == null)
            return true;

        boolean correctVideoCodec = false;
        boolean correctAudioCodec = false;

        try {
            FFprobe probe = new FFprobe(ffprobeLocation);
            FFmpegProbeResult probeResult = probe.probe(conversionJob.getInputFile().getAbsolutePath());

            logger.log(Level.INFO, probeResult.format.format_name);
            if(conversionJob.getContainerFormat() != null &&
                    !probeResult.format.format_name.toLowerCase().contains(conversionJob.getContainerFormat().name().toLowerCase())) {
                return false;
            }
            logger.log(Level.INFO, "Correct CONTAINER");

            if(conversionJob.getVideoCodec() == null)
                correctVideoCodec = true;
            if(conversionJob.getAudioCodec() == null)
                correctAudioCodec = true;

            for (FFmpegStream stream : probeResult.getStreams()) {
                String codecName = stream.codec_name;
                logger.info(conversionJob.getInputFile().getAbsolutePath() + " " + codecName);
                if (conversionJob.getAudioCodec() != null && codecName.equalsIgnoreCase(conversionJob.getAudioCodec().name()))
                    correctAudioCodec = true;
                else if (conversionJob.getVideoCodec() != null && codecName.equalsIgnoreCase(conversionJob.getVideoCodec().name()))
                    correctVideoCodec = true;
            }

        } catch (IOException e){
            logger.severe(e.toString());
        }

        return correctVideoCodec && correctAudioCodec;
    }
}