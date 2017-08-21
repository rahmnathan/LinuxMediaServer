package com.github.rahmnathan.file.converter;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class VideoControllerTest {

//    @Test
//    public void ffmpegTest() throws Exception {
//        String originalPath = "/home/nathan/development/workspaces/nathan/localmovies-server/video-converter/src/test/resources/2017-08-15-095906.webm";
//        String newPath = originalPath.substring(0, originalPath.length() - 4) + "mp4";
//
//        FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
//        FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");
//        FFmpegProbeResult in = ffprobe.probe(originalPath);
//
//        FFmpegBuilder builder = new FFmpegBuilder()
//                .setInput(originalPath)
//                .overrideOutputFiles(true)
//                .addOutput(newPath)
//                .setFormat("mp4")
//                .disableSubtitle()
//                .setAudioCodec("aac")
//                .setVideoCodec("libx264")
//                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
//                .done();
//
//        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
//        FFmpegJob job = executor.createJob(builder, new ProgressListener() {
//            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
//
//            @Override
//            public void progress(Progress progress) {
//                double percentage = progress.out_time_ms / duration_ns;
//
//                System.out.println(originalPath + " Encoding progress -> " + percentage + "%");
//            }
//        });
//
//        job.run();
//        while (true) {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                break;
//            }
//            if (job.getState() == FFmpegJob.State.FAILED) {
//                break;
//            } else if (job.getState() == FFmpegJob.State.FINISHED) {
//                break;
//            }
//        }
//    }
//
//    @Test
//    public void ffprobeTest() throws Exception {
//        VideoController videoController = new VideoController();
//        Assert.assertFalse(videoController.isCorrectFormat(new File("/home/nathan/development/workspaces/nathan/localmovies-server/video-converter/src/test/resources/2017-08-15-095906.webm")));
//        Assert.assertTrue(videoController.isCorrectFormat(new File("/home/nathan/development/workspaces/nathan/localmovies-server/video-converter/src/test/resources/test.mp4")));
//    }
}
