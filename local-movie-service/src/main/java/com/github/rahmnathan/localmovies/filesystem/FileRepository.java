package com.github.rahmnathan.localmovies.filesystem;

import java.io.File;

public interface FileRepository {
    File[] listFiles(String directoryPath);
}
