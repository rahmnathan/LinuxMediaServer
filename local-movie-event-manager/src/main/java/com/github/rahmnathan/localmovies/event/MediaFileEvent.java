package com.github.rahmnathan.localmovies.event;

import com.github.rahmnathan.localmovies.data.MediaFile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;

@Entity
public class MediaFileEvent {
    @Id
    @GeneratedValue
    private Long id;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final WatchEvent.Kind event;
    private final MediaFile mediaFile;

    public MediaFileEvent(WatchEvent.Kind event, MediaFile mediaFile) {
        this.event = event;
        this.mediaFile = mediaFile;
    }

    public Long getId() {
        return id;
    }

    public WatchEvent.Kind getEvent() {
        return event;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
