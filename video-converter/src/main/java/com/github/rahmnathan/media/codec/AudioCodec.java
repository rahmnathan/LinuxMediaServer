package com.github.rahmnathan.media.codec;

public enum AudioCodec {
    AAC("aac");

    private final String ffmpegFormat;

    AudioCodec(String ffmpegFormat){
        this.ffmpegFormat = ffmpegFormat;
    }

    public String getFfmpegFormat() {
        return ffmpegFormat;
    }
}
