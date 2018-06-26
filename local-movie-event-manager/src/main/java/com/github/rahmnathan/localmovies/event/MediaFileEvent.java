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
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String event;
    @Lob
    private final MediaFile mediaFile;

    public MediaFileEvent(String event, MediaFile mediaFile) {
        this.event = event;
        this.mediaFile = mediaFile;
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
