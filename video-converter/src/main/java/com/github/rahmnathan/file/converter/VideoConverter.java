package com.github.rahmnathan.file.converter;

import io.humble.video.*;

import java.io.File;

public class VideoConverter {

    public static boolean isCorrectFormat(File videoFile) throws Exception {
        boolean isH264 = false;
        boolean isAAC = false;

        Demuxer demuxer = Demuxer.make();
        demuxer.open(videoFile.getAbsolutePath(), null, false, true, null, null);
        for(int i = 0; i < demuxer.getNumStreams(); i++){
            DemuxerStream stream = demuxer.getStream(i);
            Codec.ID codecId = stream.getDecoder().getCodecID();
            if(codecId == Codec.ID.CODEC_ID_H264)
                isH264 = true;
            if(codecId == Codec.ID.CODEC_ID_AAC)
                isAAC = true;
        }
        demuxer.close();

        return isH264 && isAAC;
    }

    public static void convertVideo(File videoFile) throws Exception {
        int videoStreamId = -1;
        int audioStreamId = -1;
        Demuxer demuxer = Demuxer.make();
        demuxer.open(videoFile.getAbsolutePath(), null, false, true, null, null);
        for(int i = 0; i < demuxer.getNumStreams(); i++){
            MediaDescriptor.Type type = demuxer.getStream(i).getDecoder().getCodecType();
            if(type == MediaDescriptor.Type.MEDIA_AUDIO){
                audioStreamId = i;
            } else if (type == MediaDescriptor.Type.MEDIA_VIDEO){
                videoStreamId = i;
            }

            if(videoStreamId >= 0 && audioStreamId >= 0)
                break;
        }

        Muxer muxer = Muxer.make(videoFile.getAbsolutePath(), null, null);
        Encoder videoEncoder = Encoder.make(Codec.findEncodingCodec(Codec.ID.CODEC_ID_H264));
        Encoder audioEncoder = Encoder.make(Codec.findEncodingCodec(Codec.ID.CODEC_ID_AAC));
        videoEncoder.setHeight(1080);
        videoEncoder.setWidth(1920);

        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUVJ420P;
        videoEncoder.setPixelFormat(pixelformat);
        final Rational framerate = Rational.make(1, 30);
        videoEncoder.setTimeBase(framerate);

        audioEncoder.setSampleRate(22050);
        audioEncoder.setSampleFormat(AudioFormat.Type.SAMPLE_FMT_S16);

        videoEncoder.open(null, null);
        audioEncoder.open(null, null);

        muxer.addNewStream(videoEncoder);
        muxer.addNewStream(audioEncoder);

        muxer.open(null, null);

        MediaPacket packet = MediaPacket.make();

        Decoder videoDecoder = demuxer.getStream(videoStreamId).getDecoder();
        Decoder audioDecoder = demuxer.getStream(audioStreamId).getDecoder();
        muxer.addNewStream(videoDecoder);
        muxer.addNewStream(audioDecoder);
        do{
            muxer.write(packet, false);
        } while(packet.isComplete());

        demuxer.close();
        muxer.close();
    }
}