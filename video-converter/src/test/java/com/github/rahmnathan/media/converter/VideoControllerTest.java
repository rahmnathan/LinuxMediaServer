package com.github.rahmnathan.media.converter;

import com.github.rahmnathan.media.codec.AudioCodec;
import com.github.rahmnathan.media.codec.ContainerFormat;
import com.github.rahmnathan.media.codec.VideoCodec;
import com.github.rahmnathan.media.data.ConversionJob;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class VideoControllerTest {
    private final VideoController videoController = new VideoController("/usr/bin/ffmpeg", "/usr/bin/ffprobe");
    private File videoFile;

//    @Test
//    public void correctFormatTest() throws Exception {
//        videoFile = new File("/home/nathan/development/workspaces/localmovies-server/video-converter/src/test/resources/com.github.rahmnathan.media.converter/test.mp4");
//        ConversionJob job = ConversionJob.Builder.newInstance()
//                .setInputFile(videoFile)
//                .setAudioCodec(AudioCodec.AAC)
//                .setVideoCodec(VideoCodec.H264)
//                .setContainerFormat(ContainerFormat.MP4)
//                .build();
//
//        Assert.assertTrue(videoController.isCorrectFormat(job));
//
//        videoFile = new File("/home/nathan/development/workspaces/localmovies-server/video-converter/src/test/resources/com.github.rahmnathan.media.converter/test.webm");
//        job = ConversionJob.Builder.newInstance()
//                .setInputFile(videoFile)
//                .setAudioCodec(AudioCodec.AAC)
//                .setVideoCodec(VideoCodec.H264)
//                .setContainerFormat(ContainerFormat.MP4)
//                .build();
//
//        Assert.assertFalse(videoController.isCorrectFormat(job));
//    }
//
//    @Test
//    public void convertIfNecessaryTest(){
//        videoFile = new File("/home/nathan/development/workspaces/localmovies-server/video-converter/src/test/resources/com.github.rahmnathan.media.converter/test.webm");
//        ConversionJob job = ConversionJob.Builder.newInstance()
//                .setInputFile(videoFile)
//                .setOutputFile(new File(videoFile.getAbsolutePath().substring(0, videoFile.getAbsolutePath().lastIndexOf('.')) + ".mp4"))
//                .setAudioCodec(AudioCodec.AAC)
//                .setVideoCodec(VideoCodec.H264)
//                .setContainerFormat(ContainerFormat.MP4)
//                .build();
//
//        VideoConverter videoConverter = new VideoConverter(job, "/usr/bin/ffmpeg", "/usr/bin/ffprobe");
//        videoConverter.run();
//
//        Assert.assertTrue(true);
//    }

    @Test
    public void fileExtentionTest(){
        String file = "/my/test/file.mpmp";
        Assert.assertEquals(file.substring(0, file.lastIndexOf('.')) + ".mp4", "/my/test/file.mp4");
    }
}
