package com.github.rahmnathan.localmovies.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.rahmnathan.localmovies.data.MediaFile;

import javax.persistence.*;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;

@Entity
public class MediaFileEvent {
    @Id
    @GeneratedValue
    private Long id;
    @JsonSerialize()
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final String event;
    @Lob
    private final MediaFile mediaFile;

    public MediaFileEvent(WatchEvent.Kind event, MediaFile mediaFile) {
        this.event = event.toString();
        this.mediaFile = mediaFile;
    }

    public Long getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
