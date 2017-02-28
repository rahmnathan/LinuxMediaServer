package nr.localmovies.restserver;

import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
class MultipartFileSender {

    private static final int DEFAULT_BUFFER_SIZE = 80000; // ..bytes - .8mb
    private static final long DEFAULT_EXPIRE_TIME = 60480000L; // ..ms - 1.6hr
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
    private static final Logger logger = Logger.getLogger(MultipartFileSender.class.getName());

    void serveResource(Path filepath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (response == null || request == null) {
            return;
        }

        Long length = Files.size(filepath);
        String fileName = filepath.getFileName().toString();
        FileTime lastModifiedObj = Files.getLastModifiedTime(filepath);

        long lastModified = LocalDateTime.ofInstant(lastModifiedObj.toInstant(), ZoneId.of(ZoneOffset.systemDefault().getId())).toEpochSecond(ZoneOffset.UTC);
        String contentType = "video/mp4";

        // Prepare some variables. The full Range represents the complete file.
        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<>();

        // Validate and process Range and If-Range headers.
        String range = request.getHeader("Range");
        if (range != null) {

            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(fileName)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    logger.log(Level.FINE, "No If-Range header", ignore);
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte range.
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    // Assuming a file with length of 100, the following examples returns bytes at:
                    // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                    long start = Range.sublong(part, 0, part.indexOf('-'));
                    long end = Range.sublong(part, part.indexOf('-') + 1, part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    // Add range.
                    ranges.add(new Range(start, end, length));
                }
            }
        }

        // Prepare and initialize response --------------------------------------------------------

        // Get content type by file name and set content disposition.
        String disposition = "inline";

        if (!contentType.startsWith("image")) {
            // Else, expect for images, determine content disposition. If content type is supported by
            // the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
            String accept = request.getHeader("Accept");
            disposition = accept != null && HttpUtils.accepts(accept, contentType) ? "inline" : "attachment";
        }
        // Initialize response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", fileName);
        response.setDateHeader("Last-Modified", lastModified);
        response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

        // Send requested file (part(s)) to client ------------------------------------------------

        // Prepare streams.
        try (InputStream input = new BufferedInputStream(Files.newInputStream(filepath));
             OutputStream output = response.getOutputStream()) {

            if (ranges.isEmpty() || ranges.get(0) == full) {

                // Return full file.
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + full.start + "-" + full.end + "/" + full.total);
                response.setHeader("Content-Length", String.valueOf(full.length));
                Range.copy(input, output, length, full.start, full.length);

            } else if (ranges.size() == 1) {

                // Return single part of file.
                Range r = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                // Copy single part range.
                Range.copy(input, output, length, r.start, r.length);

            } else {
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                ServletOutputStream sos = (ServletOutputStream) output;

                for (Range r : ranges) {
                    // Add multipart boundary and header fields for every range.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY);
                    sos.println("Content-Type: " + contentType);
                    sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

                    // Copy single part range of multi part range.
                    Range.copy(input, output, length, r.start, r.length);
                }

                // End with multipart boundary.
                sos.println();
                sos.println("--" + MULTIPART_BOUNDARY + "--");
            }
        }

    }

    private static class Range {
        long start;
        long end;
        long length;
        long total;

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

        static long sublong(String value, int beginIndex, int endIndex) {
            String substring = value.substring(beginIndex, endIndex);
            return (substring.length() > 0) ? Long.parseLong(substring) : -1;
        }

        private static void copy(InputStream input, OutputStream output, long inputSize, long start, long length) throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;

            if (inputSize == length) {
                // Write full range.
                while ((read = input.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                    output.flush();
                }
            } else {
                input.skip(start);
                long toRead = length;

                while ((read = input.read(buffer)) > 0) {
                    if ((toRead -= read) > 0) {
                        output.write(buffer, 0, read);
                        output.flush();
                    } else {
                        output.write(buffer, 0, (int) toRead + read);
                        output.flush();
                        break;
                    }
                }
            }
        }
    }
    private static class HttpUtils {
        /**
         * Returns true if the given accept header accepts the given value.
         * @param acceptHeader The accept header.
         * @param toAccept The value to be accepted.
         * @return True if the given accept header accepts the given value.
         */
        static boolean accepts(String acceptHeader, String toAccept) {
            String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
            Arrays.sort(acceptValues);

            return Arrays.binarySearch(acceptValues, toAccept) > -1
                    || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
                    || Arrays.binarySearch(acceptValues, "*/*") > -1;
        }
    }
}