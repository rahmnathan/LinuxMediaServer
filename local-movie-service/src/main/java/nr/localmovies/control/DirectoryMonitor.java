package nr.localmovies.control;

import com.sun.nio.file.SensitivityWatchEventModifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private FileListProvider fileListProvider;

    @Autowired
    public DirectoryMonitor(FileListProvider fileListProvider){
        this.fileListProvider = fileListProvider;
    }

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

        final Map<WatchKey, Path> keys = new HashMap<>();

        Consumer<Path> register = p -> {
            if (!p.toFile().exists() || !p.toFile().isDirectory()) {
                throw new RuntimeException("folder " + p + " does not exist or is not a directory");
            }
            try {
                Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        logger.info("registering " + dir + " in watcher service");
                        WatchKey watchKey = dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
                        keys.put(watchKey, dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Error registering path " + p);
            }
        };

        register.accept(Paths.get("/home/nathan/LocalMedia/"));

        executor.submit(() -> {
            while (true) {
                final WatchKey key;
                try {
                    key = watcher.take(); // wait for a key to be available
                } catch (InterruptedException ex) {
                    return;
                }

                final Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey " + key + " not recognized!");
                    continue;
                }

                key.pollEvents().stream()
                        .filter(e -> (e.kind() != OVERFLOW))
                        .map(e -> ((WatchEvent<Path>) e).context())
                        .forEach(p -> {
                            final Path absPath = dir.resolve(p);
                            if (absPath.toFile().isDirectory()) {
                                register.accept(absPath);
                            } else {
                                final File f = absPath.toFile();
                                logger.info("Detected new file " + f.getAbsolutePath());
                            }
                        });

                fileListProvider.purgeTitleCache();
                boolean valid = key.reset(); // IMPORTANT: The key must be reset after processed
                if (!valid) {
                    break;
                }
            }
        });
    }
}