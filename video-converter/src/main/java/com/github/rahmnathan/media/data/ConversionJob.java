package com.github.rahmnathan.media.data;

import com.github.rahmnathan.media.codec.AudioCodec;
import com.github.rahmnathan.media.codec.ContainerFormat;
import com.github.rahmnathan.media.codec.VideoCodec;

import java.io.File;

public class ConversionJob {
    private final VideoCodec videoCodec;
    private final AudioCodec audioCodec;
    private final ContainerFormat containerFormat;
    private final File inputFile;
    private final File outputFile;

    private ConversionJob(VideoCodec videoCodec, AudioCodec audioCodec, ContainerFormat containerFormat, File inputFile, File outputFile) {
        this.videoCodec = videoCodec;
        this.audioCodec = audioCodec;
        this.containerFormat = containerFormat;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public VideoCodec getVideoCodec() {
        return videoCodec;
    }

    public AudioCodec getAudioCodec() {
        return audioCodec;
    }

    public ContainerFormat getContainerFormat() {
        return containerFormat;
    }

    public File getInputFile() {
        return inputFile;
    }

    public static class Builder {
        private VideoCodec videoCodec;
        private AudioCodec audioCodec;
        private ContainerFormat containerFormat;
        private File inputFile;
        private File outputFile;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setOutputFile(File outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder setVideoCodec(VideoCodec videoCodec) {
            this.videoCodec = videoCodec;
            return this;
        }

        public Builder setAudioCodec(AudioCodec audioCodec) {
            this.audioCodec = audioCodec;
            return this;
        }

        public Builder setContainerFormat(ContainerFormat containerFormat) {
            this.containerFormat = containerFormat;
            return this;
        }

        public Builder setInputFile(File inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public ConversionJob build(){
            return new ConversionJob(videoCodec, audioCodec, containerFormat, inputFile, outputFile);
        }
    }
}
