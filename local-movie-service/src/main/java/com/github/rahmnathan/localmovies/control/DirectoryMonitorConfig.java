package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.directory.monitor.DirectoryMonitor;
import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;

@Component
public class DirectoryMonitorConfig {

    @Value("${media.path}")
    private String[] mediaPaths;
    private final DirectoryMonitor directoryMonitor;

    @Autowired
    public DirectoryMonitorConfig(Collection<DirectoryMonitorObserver> observers) {
        this.directoryMonitor = new DirectoryMonitor(observers);
    }

    @PostConstruct
    public void startDirectoryMonitor() {
        Arrays.stream(mediaPaths).forEach(directoryMonitor::registerDirectory);
    }
}
