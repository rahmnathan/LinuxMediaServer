package com.github.rahmnathan.video.job;

import com.github.rahmnathan.video.codec.AudioCodec;
import com.github.rahmnathan.video.codec.VideoCodec;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

import java.io.File;

public class SimpleConversionJob {
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final File inputFile;
    private final File outputFile;
    private final AudioCodec audioCodec;
    private final VideoCodec videoCodec;

    private SimpleConversionJob(File inputFile, File outputFile, FFmpeg ffmpeg, FFprobe ffprobe,
                                AudioCodec audioCodec, VideoCodec videoCodec) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.ffmpeg = ffmpeg;
        this.ffprobe = ffprobe;
        this.audioCodec = audioCodec;
        this.videoCodec = videoCodec;
    }

    public AudioCodec getAudioCodec() {
        return audioCodec;
    }

    public VideoCodec getVideoCodec() {
        return videoCodec;
    }

    public FFmpeg getFfmpeg() {
        return ffmpeg;
    }

    public FFprobe getFfprobe() {
        return ffprobe;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getInputFile() {
        return inputFile;
    }

    public static class Builder {
        private File inputFile;
        private File outputFile;
        private FFmpeg ffmpeg;
        private FFprobe ffprobe;
        private VideoCodec videoCodec;
        private AudioCodec audioCodec;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder setVideoCodec(VideoCodec videoCodec) {
            this.videoCodec = videoCodec;
            return this;
        }

        public Builder setAudioCodec(AudioCodec audioCodec) {
            this.audioCodec = audioCodec;
            return this;
        }

        public Builder setFfmpeg(FFmpeg ffmpeg) {
            this.ffmpeg = ffmpeg;
            return this;
        }

        public Builder setFfprobe(FFprobe ffprobe) {
            this.ffprobe = ffprobe;
            return this;
        }

        public Builder setOutputFile(File outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder setInputFile(File inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public SimpleConversionJob build() {
            return new SimpleConversionJob(inputFile, outputFile, ffmpeg, ffprobe, audioCodec, videoCodec);
        }
    }
}