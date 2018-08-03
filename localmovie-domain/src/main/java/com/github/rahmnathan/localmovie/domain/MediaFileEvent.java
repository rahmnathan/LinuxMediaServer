package com.github.rahmnathan.localmovie.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MediaFileEvent {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String relativePath;
    private String event;
    @JoinColumn
    @ManyToOne
    private MediaFile mediaFile;

    public MediaFileEvent() {
    }

    public MediaFileEvent(String event, MediaFile mediaFile, String relativePath) {
        this.relativePath = relativePath;
        this.mediaFile = mediaFile;
        this.event = event;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getEvent() {
        return event;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
