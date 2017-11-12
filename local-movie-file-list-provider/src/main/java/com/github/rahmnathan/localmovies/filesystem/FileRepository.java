package com.github.rahmnathan.localmovies.filesystem;

import java.util.Set;

public interface FileRepository {
    Set<String> listFiles(String directoryPath);
}
