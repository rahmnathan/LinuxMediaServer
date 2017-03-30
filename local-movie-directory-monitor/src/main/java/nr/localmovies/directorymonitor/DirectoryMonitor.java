package nr.localmovies.directorymonitor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class DirectoryMonitor {
    private WatchService watcher;
    private Map<WatchKey, Path> keys;
    private Logger logger = Logger.getLogger(DirectoryMonitor.class.getName());
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void watchDirectories(){
        executorService.submit(this::processEvents);
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    public void registerAll(String path) {
        try {
            Path parentPath = Paths.get(path);
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<>();
            Files.walkFileTree(parentPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e){
            logger.severe("Failed to register directory - " + path);
        }
    }

    private void processEvents() {
        while (true) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            keys.get(key);

            for (WatchEvent<?> event : key.pollEvents()) {
                purgeTitleCache();
            }
        }
    }

    @CacheEvict(value = "files", allEntries = true)
    public void purgeTitleCache(){
        logger.log(Level.INFO, "Purging cache");
    }
}