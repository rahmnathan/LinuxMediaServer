package nr.localmovies.control;

import nr.localmovies.directorymonitor.DirectoryMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.logging.Logger;

@Component
public class FileLister implements FileRepository {

    Logger logger = Logger.getLogger(FileLister.class.getName());
    private DirectoryMonitor directoryMonitor;

    @Autowired
    public FileLister(DirectoryMonitor directoryMonitor){
        this.directoryMonitor = directoryMonitor;
    }

    @Cacheable("files")
    public File[] listFiles(String directoryPath) {
        logger.info("Listing files at - " + directoryPath);
        File[] files = new File(directoryPath).listFiles();
        if(files == null || files.length == 0)
            files = new File[0];

        return files;
    }
}
