package com.github.rahmnathan.localmovies.web.control;

import com.github.rahmnathan.localmovies.boundary.MovieInfoFacade;
import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.pushnotification.control.MoviePushNotificationHandler;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushClient;
import com.github.rahmnathan.localmovies.web.data.MovieInfoRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class MovieResource {

    @Value("${media.path}")
    private String[] mediaPaths;
    private final MovieInfoFacade movieInfoFacade;
    private final MoviePushNotificationHandler notificationHandler;
    private final FileSender fileSender = new FileSender();
    private final Logger logger = Logger.getLogger(MovieResource.class.getName());

    @Autowired
    public MovieResource(MovieInfoFacade movieInfoControl, MoviePushNotificationHandler notificationHandler){
        this.movieInfoFacade = movieInfoControl;
        this.notificationHandler = notificationHandler;
    }

    @RequestMapping(value = "/titlerequest", method = RequestMethod.POST, produces="application/json", consumes = "application/json")
    public List<MediaFile> titleRequest(@RequestBody MovieInfoRequest movieInfoRequest, HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        MDC.put("Client-Address", request.getRemoteAddr());
        logger.info("Received request: " + movieInfoRequest.toString());

        if(movieInfoRequest.getPushToken() != null && movieInfoRequest.getDeviceId() != null){
            AndroidPushClient pushClient = new AndroidPushClient(movieInfoRequest.getDeviceId(), movieInfoRequest.getPushToken());
            notificationHandler.addPushToken(pushClient);
        }

        // Using file-system specific file separator
        String path = movieInfoRequest.getPath().replace("/", File.separator);

        MovieSearchCriteria searchCriteria = MovieSearchCriteria.Builder.newInstance()
                .setItemsPerPage(movieInfoRequest.getResultsPerPage())
                .setOrder(movieInfoRequest.getOrder())
                .setPage(movieInfoRequest.getPage())
                .setPath(path)
                .setClient(movieInfoRequest.getClient())
                .build();

        if(searchCriteria.getPage() == 0)
            movieInfoCount(movieInfoRequest.getPath(), response, request);

        List<MediaFile> movieInfoList = movieInfoFacade.loadMovieList(searchCriteria);

        logger.info("Returning " + movieInfoList.size() + " movies");
        MDC.clear();
        return movieInfoList;
    }

    /**
     * @param path - Path to directory you wish to count files from
     */
    @RequestMapping(value = "/movieinfocount")
    public void movieInfoCount(@RequestParam(value = "path") String path, HttpServletResponse response, HttpServletRequest request){
        MDC.put("Client-Address", request.getRemoteAddr());

        // Using file-system specific file separator
        path = path.replace("/", File.separator);
        logger.log(Level.INFO, "Received request for count for - " + path);

        int count = movieInfoFacade.loadMovieListLength(path);
        logger.log(Level.INFO, "Returning count of - " + count);
        response.setHeader("Count", String.valueOf(count));
        MDC.clear();
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

        MediaFile movie = movieInfoFacade.loadSingleMovie(path);
        movie.addView();
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
     * @param path - Path to video file
     * @return - Poster image for specified video file
     */
    @RequestMapping("/poster")
    public byte[] servePoster(@RequestParam("path") String path, HttpServletResponse response, HttpServletRequest request) {
        MDC.put("Client-Address", request.getRemoteAddr());
        response.addHeader("Access-Control-Allow-Origin", "*");

        // Using file-system specific file separator
        path = path.replace("/", File.separator);
        logger.info("Streaming poster " + path);

        String image = movieInfoFacade.loadSingleMovie(path).getMovieInfo().getImage();
        if(image == null)
            return new byte[0];

        byte[] poster = Base64.getDecoder().decode(image);
        MDC.clear();
        return poster;
    }
}