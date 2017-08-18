package com.github.rahmnathan.file.converter;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoConverter implements Runnable {
    private final File videoFile;
    private volatile Set<String> convertedFiles;
    private final Logger logger = Logger.getLogger(VideoConverter.class.getName());

    public VideoConverter(File videoFile, Set<String> convertedFiles) {
        this.videoFile = videoFile;
        this.convertedFiles = convertedFiles;
    }

    @Override
    public void run() {
        String originalPath = videoFile.getAbsolutePath();
        String newPath = originalPath.substring(0, originalPath.length() - 3) + "mp4";
        convertedFiles.add(newPath);
        try{
            Process process = new ProcessBuilder("HandBrakeCLI",  "-i", originalPath, "-o", newPath).start();
            process.waitFor();
            int status = process.exitValue();
            logger.log(Level.INFO, "Conversion exit code: " + status);
            if(status == 0) {
                videoFile.delete();
            }
        } catch (IOException | InterruptedException e){
            logger.severe(e.toString());
        }
    }
}