package com.github.rahmnathan.localmovies.directorymonitor;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface DirectoryMonitorObserver {
    void directoryModified(WatchEvent event, Path absolutePath);
}
