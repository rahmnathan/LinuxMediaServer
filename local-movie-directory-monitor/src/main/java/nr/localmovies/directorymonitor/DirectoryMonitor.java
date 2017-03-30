package nr.localmovies.directorymonitor;

import com.sun.nio.file.SensitivityWatchEventModifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Service
public class DirectoryMonitor {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryMonitor.class);
    private WatchService watcher;
    private ExecutorService executor;

    @PostConstruct
    public void init() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        executor = Executors.newSingleThreadExecutor();
        startRecursiveWatcher();
    }

    @PreDestroy
    public void cleanup() {
        try {
            watcher.close();
        } catch (IOException e) {
            logger.error("Error closing watcher service", e);
        }
        executor.shutdown();
    }

    private void startRecursiveWatcher() throws IOException {
        logger.info("Starting Recursive Watcher");

        Consumer<Path> register = path -> {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        logger.info("registering " + dir + " in watcher service");
                        dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.info("Error registering path " + path);
            }
        };

        register.accept(Paths.get("/home/nathan/LocalMedia/"));

        executor.submit(() -> {
            while (true) {
                try {
                    watcher.take(); // wait for a key to be available
                } catch (InterruptedException ex) {
                    logger.error("Directory watcher interrupted");
                    return;
                }

                purgeTitleCache();
            }
        });
    }

    @CacheEvict(value = "files", allEntries = true)
    public void purgeTitleCache(){
        logger.info("Purging cache");
    }
}