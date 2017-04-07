package nr.localmovies.directory.monitor;

import com.sun.nio.file.SensitivityWatchEventModifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class DirectoryMonitor {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryMonitor.class);
    private WatchService watcher;
    private ExecutorService executor;
    private List<DirectoryMonitorObserver> observerList = new ArrayList<>();
    private final Map<WatchKey, Path> keys = new HashMap<>();

    public void addObserver(DirectoryMonitorObserver observer){
        observerList.add(observer);
    }

    private void notifyObservers(){
        observerList.forEach(DirectoryMonitorObserver::directoryModified);
    }

    @PostConstruct
    public void init() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        executor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void cleanup() {
        try {
            logger.info("Stopping directory monitor");
            watcher.close();
        } catch (IOException e) {
            logger.error("Error closing watcher service", e);
        }
        executor.shutdown();
    }

    @SuppressWarnings("unchecked")
    public void startRecursiveWatcher(String pathToMonitor) {
        logger.info("Starting Recursive Watcher");

        Consumer<Path> register = p -> {
            if (!p.toFile().exists() || !p.toFile().isDirectory())
                throw new RuntimeException("folder " + p + " does not exist or is not a directory");

            try {
                Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        logger.info("registering " + dir + " in watcher service");
                        WatchKey watchKey = dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
                        keys.put(watchKey, dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Error registering path " + p);
            }
        };

        register.accept(Paths.get(pathToMonitor));

        executor.submit(() -> {
            while (true) {
                final WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    logger.error(ex.toString());
                    continue;
                }

                final Path dir = keys.get(key);

                key.pollEvents().stream()
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

                notifyObservers();
                key.reset();
            }
        });
    }
}