package nr.localmovies.control;

import java.io.File;

public interface FileRepository {
    File[] listFiles(String directoryPath);
    void purgeTitleCache();
}
