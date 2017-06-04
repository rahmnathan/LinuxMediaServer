package com.github.rahmnathan.directorymonitor;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface DirectoryMonitorObserver {
    void directoryModified(WatchEvent event, Path absolutePath);
}
