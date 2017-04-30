package nr.localmovies.directory.monitor;

import com.sun.nio.file.SensitivityWatchEventModifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Service
public class DirectoryMonitor {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryMonitor.class);
    private ExecutorService executor;
    private List<DirectoryMonitorObserver> observerList;
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private WatchService watchService;

    private void notifyObservers(){
        observerList.forEach(DirectoryMonitorObserver::directoryModified);
    }

    @Autowired
    public DirectoryMonitor(List<DirectoryMonitorObserver> observerList) {
        this.observerList = observerList;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void cleanup() {
        executor.shutdown();
    }

    @SuppressWarnings("unchecked")
    public void startRecursiveWatcher(String pathToMonitor) {
        logger.info("Starting Recursive Watcher");
        
        try{
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e){
            logger.error(e.toString());
            return;
        }

        Consumer<Path> register = p -> {
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
                logger.error(e.toString());
            }
        };

        register.accept(Paths.get(pathToMonitor));

        executor.submit(() -> {
            while (true) {
                final WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException ex) {
                    logger.error(ex.toString());
                    continue;
                }

                final Path dir = keys.get(key);

                key.pollEvents().stream()
                        .map(e -> ((WatchEvent<Path>) e))
                        .forEach(p -> {
                            if (!p.kind().equals(OVERFLOW)) {
                                final Path absPath = dir.resolve(p.context());
                                if (absPath.toFile().isDirectory()) {
                                    register.accept(absPath);
                                } else {
                                    logger.info("Detected " + p.kind().name() + " " + absPath.toString());
                                }
                            }
                        });

                notifyObservers();
                key.reset();
            }
        });
    }
}