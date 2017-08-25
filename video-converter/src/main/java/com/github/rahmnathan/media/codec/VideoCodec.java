package com.github.rahmnathan.media.codec;

public enum VideoCodec {
    H264("libx264");

    private final String ffmpegFormat;

    VideoCodec(String ffmpegFormat){
        this.ffmpegFormat = ffmpegFormat;
    }

    public String getFfmpegFormat() {
        return ffmpegFormat;
    }
}
