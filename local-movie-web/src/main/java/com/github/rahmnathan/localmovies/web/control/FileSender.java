package com.github.rahmnathan.localmovies.web.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class FileSender {

    private static final int DEFAULT_BUFFER_SIZE = 16384;
    private final Logger logger = Logger.getLogger(FileSender.class.getName());

    public void serveResource(Path file, HttpServletRequest request, HttpServletResponse response) {
        if (response == null || request == null || file == null)
            return;

        long totalBytes = 0L;
        try {
            totalBytes = Files.size(file);
        } catch (IOException e) {
            logger.severe(e.toString());
        }

        long startByte = 0L;
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            startByte = Long.parseLong(rangeHeader.split("-")[0].substring(6));
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }

        response.setHeader("Content-Range", "bytes " + startByte + "-" + (totalBytes - 1) + "/" + totalBytes);
        response.setHeader("Content-Length", String.valueOf(totalBytes - startByte));

        streamFile(file, response, startByte);
    }

    private void streamFile(Path file, HttpServletResponse response, long startByte){
        try (InputStream input = new BufferedInputStream(Files.newInputStream(file));
             OutputStream output = response.getOutputStream()) {

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            input.skip(startByte);
            while ((input.read(buffer)) > 0) {
                output.write(buffer);
                output.flush();
            }

        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }
}