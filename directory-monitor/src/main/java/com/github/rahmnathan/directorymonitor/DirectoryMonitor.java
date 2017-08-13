package com.github.rahmnathan.directorymonitor;

import com.sun.nio.file.SensitivityWatchEventModifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Service
public class DirectoryMonitor {

    private static final Logger logger = Logger.getLogger(DirectoryMonitor.class.getName());
    private final ExecutorService executor;
    private final List<DirectoryMonitorObserver> observerList;
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private WatchService watchService;
    private Consumer<Path> register;

    @Autowired
    public DirectoryMonitor(List<DirectoryMonitorObserver> observerList) {
        this.observerList = observerList;
        this.executor = Executors.newSingleThreadExecutor();
        startRecursiveWatcher();
    }

    @PreDestroy
    public void cleanup() {
        executor.shutdown();
    }

    private void notifyObservers(WatchEvent event, Path absolutePath) {
        observerList.forEach(observer -> observer.directoryModified(event, absolutePath));
    }

    public void registerDirectory(String pathToMonitor) {
        register.accept(Paths.get(pathToMonitor));
    }

    @SuppressWarnings("unchecked")
    private void startRecursiveWatcher() {
        logger.info("Starting Recursive Watcher");

        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.severe(e.toString());
            return;
        }

        register = p -> {
            try {
                Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        logger.info("registering " + dir + " in watcher service");
                        WatchKey watchKey = dir.register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
                        keys.put(watchKey, dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.severe(e.toString());
            }
        };

        executor.submit(() -> {
            while (true) {
                final WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    logger.severe(e.toString());
                    continue;
                }

                final Path dir = keys.get(key);

                key.pollEvents().stream()
                        .map(e -> ((WatchEvent<Path>) e))
                        .forEach(event -> {
                            if (!event.kind().equals(OVERFLOW)) {
                                final Path absPath = dir.resolve(event.context());
                                notifyObservers(event, absPath);
                                if (absPath.toFile().isDirectory()) {
                                    register.accept(absPath);
                                }
                            }
                        });
                key.reset();
            }
        });
    }
}