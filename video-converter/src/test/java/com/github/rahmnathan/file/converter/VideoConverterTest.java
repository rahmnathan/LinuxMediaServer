package com.github.rahmnathan.file.converter;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class VideoConverterTest {

    @Test
    public void isCorrectFormatTest() throws Exception {
        File videoFile = new File("/home/nathan/development/workspaces/localmovies-server/video-converter/src/test/resources/episode.mkv");
        IContainer container = IContainer.make();
        if(container.open(videoFile.getPath(),IContainer.Type.READ,null) < 0) {
            Assert.fail();
        }

        // query how many streams the call to open found
        boolean isH264 = false;
        boolean isAAC = false;

        int numStreams = container.getNumStreams();
        for(int i = 0; i < numStreams; i++) {
            // find the stream object
            IStream stream = container.getStream(i);
            // get the pre-configured decoder that can decode this stream;
            IStreamCoder coder = stream.getStreamCoder();

            if (coder.getCodecID() == ICodec.ID.CODEC_ID_H264)  {
                isH264 = true;
            }
            if (coder.getCodecID() == ICodec.ID.CODEC_ID_AAC)  {
                isAAC = true;
            }
        }

        if (container !=null) {
            container.close();
        }

        Assert.assertTrue(isH264 && isAAC);
    }
}
