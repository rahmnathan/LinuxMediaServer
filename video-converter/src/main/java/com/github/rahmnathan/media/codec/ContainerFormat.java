package com.github.rahmnathan.media.codec;

public enum ContainerFormat {
    MP4("mp4");

    private final String ffmpegFormat;

    ContainerFormat(String ffmpegFormat){
        this.ffmpegFormat = ffmpegFormat;
    }

    public String getFfmpegFormat() {
        return ffmpegFormat;
    }
}
