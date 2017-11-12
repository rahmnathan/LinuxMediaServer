package com.github.rahmnathan.localmovies.web.config;

import com.github.rahmnathan.directory.monitor.DirectoryMonitor;
import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;

@Component
public class DirectoryMonitorInitializer {
    @Value("${media.path}")
    private String[] mediaPaths;
    private DirectoryMonitor directoryMonitor;

    @Autowired
    public DirectoryMonitorInitializer(Collection<DirectoryMonitorObserver> observers){
        directoryMonitor = new DirectoryMonitor(observers);
    }

    @PostConstruct
    public void initialize(){
        Arrays.stream(mediaPaths).forEach(directoryMonitor::registerDirectory);
    }
}
