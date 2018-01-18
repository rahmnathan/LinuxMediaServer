package com.github.rahmnathan.localmovies.service.filesystem;

import java.util.Set;

public interface FileRepository {
    Set<String> listFiles(String directoryPath);
}
