package com.github.rahmnathan.localmovies.web.control;

import com.github.rahmnathan.localmovies.filesystem.FileRepository;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Logger;

@RestController
public class VideoResource {

    @Value("${media.path}")
    private String[] mediaPaths;
    private final FileSender fileSender = new FileSender();
    private final FileRepository fileListProvider;
    private final Logger logger = Logger.getLogger(VideoResource.class.getName());

    @Autowired
    public VideoResource(FileRepository fileListProvider){
        this.fileListProvider = fileListProvider;
    }

    /**
     * @param path - Path to video file to stream
     */
    @RequestMapping(value = "/video.mp4", produces = "video/mp4")
    public void streamVideo(@RequestParam("path") String path, HttpServletResponse response, HttpServletRequest request) {
        MDC.put("Client-Address", request.getRemoteAddr());
        response.addHeader("Access-Control-Allow-Origin", "*");

        // Using file-system specific file separator
        path = path.replace("/", File.separator);
        logger.info("Received streaming request - " + path);
        for(String mediaPath : mediaPaths) {
            if (new File(mediaPath + path).exists()) {
                logger.info("Streaming - " + mediaPath + path);
                fileSender.serveResource(Paths.get(mediaPath + path), request, response);
                break;
            }
        }
        MDC.clear();
    }

    /**
     * @param path - Relative path to list
     */
    @RequestMapping(value = "/filelist", produces = "application/json")
    public Set<String> listFiles(@RequestParam("path") String path, HttpServletResponse servletResponse, HttpServletRequest servletRequest) {
        MDC.put("Client-Address", servletRequest.getRemoteAddr());
        servletResponse.addHeader("Access-Control-Allow-Origin", "*");

        // Using file-system specific file separator
        path = path.replace("/", File.separator);

        logger.info("Received streaming request - " + path);
        Set<String> files =  fileListProvider.listFiles(path);
        MDC.clear();
        return files;
    }
}
