package com.github.rahmnathan.localmovies.control;

import java.io.File;

public interface FileRepository {
    File[] listFiles(String directoryPath);
}
