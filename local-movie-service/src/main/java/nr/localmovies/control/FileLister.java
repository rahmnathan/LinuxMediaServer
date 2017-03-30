package nr.localmovies.control;

import nr.localmovies.directorymonitor.DirectoryMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileLister implements FileRepository {

    private DirectoryMonitor directoryMonitor;

    @Autowired
    public FileLister(DirectoryMonitor directoryMonitor){
        this.directoryMonitor = directoryMonitor;
    }

    @Cacheable("files")
    public File[] listFiles(String directoryPath) {
        directoryMonitor.registerAll(directoryPath);
        File[] files = new File(directoryPath).listFiles();
        if(files == null || files.length == 0)
            files = new File[0];

        return files;
    }
}
