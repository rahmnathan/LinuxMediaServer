package com.github.rahmnathan.localmovies.web;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Component
class FileSender {

    private static final int DEFAULT_BUFFER_SIZE = 16384;
    private final Logger logger = Logger.getLogger(FileSender.class.getName());

    void serveResource(Path filepath, HttpServletRequest request, HttpServletResponse response) {
        if (response == null || request == null)
            return;

        Long length;
        try {
            length = Files.size(filepath);
        } catch (IOException e) {
            length = 0L;
            logger.info(e.toString());
        }
        Range range;
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            long start = Long.valueOf(rangeHeader.split("-")[0].substring(6));
            range = new Range(start, length - 1, length);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        } else {
            range = new Range(0, length - 1, length);
        }

        response.setHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + range.total);
        response.setHeader("Content-Length", String.valueOf(range.length));
        try (InputStream input = new BufferedInputStream(Files.newInputStream(filepath));
             OutputStream output = response.getOutputStream()) {
            Range.copy(input, output, length, range.start, range.length);
        } catch (IOException e){
            logger.fine(e.toString());
        }
    }

    private static class Range {
        final long start;
        final long end;
        final long length;
        final long total;

        /**
         * Construct a byte range.
         * @param start Start of the byte range.
         * @param end End of the byte range.
         * @param total Total length of the byte source.
         */
        Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

        private static void copy(InputStream input, OutputStream output, long inputSize, long start, long length) throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;

            if (inputSize == length) {
                while ((read = input.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                    output.flush();
                }
            } else {
                input.skip(start);
                long toRead = length;

                while ((read = input.read(buffer)) > 0) {
                    if ((toRead -= read) > 0) {
                        output.write(buffer);
                        output.flush();
                    } else {
                        output.write(buffer);
                        output.flush();
                        break;
                    }
                }
            }
        }
    }
}