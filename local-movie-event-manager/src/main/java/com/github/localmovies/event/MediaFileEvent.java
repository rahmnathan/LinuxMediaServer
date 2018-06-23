package com.github.localmovies.event;

import com.github.rahmnathan.localmovies.data.MediaFile;

import java.nio.file.WatchEvent;
import java.time.LocalDateTime;

public class MediaFileEvent {
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final WatchEvent.Kind event;
    private final MediaFile mediaFile;

    public MediaFileEvent(WatchEvent.Kind event, MediaFile mediaFile) {
        this.event = event;
        this.mediaFile = mediaFile;
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
