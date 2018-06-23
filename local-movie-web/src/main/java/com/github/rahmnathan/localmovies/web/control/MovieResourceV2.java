package com.github.rahmnathan.localmovies.web.control;

import com.github.rahmnathan.localmovies.data.MediaFile;
import com.github.rahmnathan.localmovies.pushnotification.control.MoviePushNotificationHandler;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushClient;
import com.github.rahmnathan.localmovies.service.boundary.MovieInfoFacade;
import com.github.rahmnathan.localmovies.service.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.web.data.MovieInfoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
public class MovieResourceV2 {
    private final Logger logger = LoggerFactory.getLogger(MovieResourceV2.class.getName());
    private final MoviePushNotificationHandler notificationHandler;
    private static final String TRANSACTION_ID = "TransactionID";
    private final FileSender fileSender = new FileSender();
    private final MovieInfoFacade movieInfoFacade;
    private final String[] mediaPaths;

    public MovieResourceV2(MovieInfoFacade movieInfoControl, MoviePushNotificationHandler notificationHandler,
                           @Value("${media.path}") String[] mediaPaths){
        this.notificationHandler = notificationHandler;
        this.movieInfoFacade = movieInfoControl;
        this.mediaPaths = mediaPaths;
    }

    @RequestMapping(value = "/v2/movies", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MediaFile> titleRequest(@RequestBody MovieInfoRequest movieInfoRequest, HttpServletResponse response) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        logger.info("Received request: {}", movieInfoRequest.toString());

        if(movieInfoRequest.getPushToken() != null && movieInfoRequest.getDeviceId() != null){
            AndroidPushClient pushClient = new AndroidPushClient(movieInfoRequest.getDeviceId(), movieInfoRequest.getPushToken());
            notificationHandler.addPushToken(pushClient);
        }

        MovieSearchCriteria searchCriteria = new MovieSearchCriteria(movieInfoRequest.getPath(), movieInfoRequest.getPage(),
                movieInfoRequest.getResultsPerPage(), movieInfoRequest.getClient(), movieInfoRequest.getOrder());

        Integer page = searchCriteria.getPage();
        if(page != null && page == 0)
            movieInfoCount(movieInfoRequest.getPath(), response);

        List<MediaFile> movieInfoList = movieInfoFacade.loadMovieList(searchCriteria);

        logger.info("Returning {} movies", movieInfoList.size());
        MDC.clear();
        return movieInfoList;
    }

    @RequestMapping(value = "/v2/movies/count")
    public void movieInfoCount(@RequestParam(value = "path") String path, HttpServletResponse response){
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());

        logger.info("Received count request for path - {}", path);

        int count = movieInfoFacade.loadMovieListLength(path);
        logger.info("Returning count of - {}", count);
        response.setHeader("Count", String.valueOf(count));
        MDC.clear();
    }

    /**
     * @param path - Path to video file to stream
     */
    @RequestMapping(value = "/v2/movie/stream.mp4", produces = "video/mp4")
    public void streamVideo(@RequestParam("path") String path, HttpServletResponse response, HttpServletRequest request) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        response.setHeader("Access-Control-Allow-Origin", "*");

        MediaFile movie = movieInfoFacade.loadSingleMovie(path);
        movie.addView();
        logger.info("Received streaming request - {}", path);
        for(String mediaPath : mediaPaths) {
            if (new File(mediaPath + path).exists()) {
                logger.info("Streaming - {}{}", mediaPath, path);
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
    @RequestMapping("/v2/movie/poster")
    public byte[] servePoster(@RequestParam("path") String path) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());

        // Using file-system specific file separator
        path = path.replace("/", File.separator);
        logger.info("Streaming poster - {}", path);

        String image = movieInfoFacade.loadSingleMovie(path).getMovie().getImage();
        if(image == null)
            return new byte[0];

        byte[] poster = Base64.getDecoder().decode(image);
        MDC.clear();
        return poster;
    }
}