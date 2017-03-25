package nr.localmovies.control;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileLister implements FileRepository {

    @Cacheable("files")
    public File[] listFiles(String directoryPath) {
        File[] files = new File(directoryPath).listFiles();
        if(files == null || files.length == 0)
            files = new File[0];

        return files;
    }
}
