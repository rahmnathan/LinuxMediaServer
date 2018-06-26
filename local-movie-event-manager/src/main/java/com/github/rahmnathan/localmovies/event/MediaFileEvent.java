package com.github.rahmnathan.localmovies.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.rahmnathan.localmovies.data.MediaFile;

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
    private final String relativePath;
    private final String event;
    @Lob
    private final MediaFile mediaFile;

    public MediaFileEvent(String event, MediaFile mediaFile, String relativePath) {
        this.relativePath = relativePath;
        this.mediaFile = mediaFile;
        this.event = event;
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
