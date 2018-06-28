package com.github.rahmnathan.localmovies.service.config;

import com.github.rahmnathan.directory.monitor.DirectoryMonitor;
import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.ManagedBean;
import java.util.Arrays;
import java.util.Collection;

@ManagedBean
public class DirectoryMonitorConfig {

    public DirectoryMonitorConfig(Collection<DirectoryMonitorObserver> observers, @Value("${media.path}") String[] mediaPaths) {
        DirectoryMonitor directoryMonitor = new DirectoryMonitor(observers);
        Arrays.stream(mediaPaths).forEach(directoryMonitor::registerDirectory);
    }
}
