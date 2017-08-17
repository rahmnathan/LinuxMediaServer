package com.github.rahmnathan.file.converter;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class VideoConverterTest {

    @Test
    public void isCorrectFormatTest() throws Exception {
        File videoFile = new File("/home/nathan/development/workspaces/nathan/localmovies-server/video-converter/src/test/resources/2017-08-15-095906.webm");

        Assert.assertFalse(VideoConverter.isCorrectFormat(videoFile));
    }

    @Test
    public void convertVideoTest() throws Exception {
        File videoFile = new File("/home/nathan/development/workspaces/nathan/localmovies-server/video-converter/src/test/resources/2017-08-15-095906.webm");

        VideoConverter.convertVideo(videoFile);
    }
}
