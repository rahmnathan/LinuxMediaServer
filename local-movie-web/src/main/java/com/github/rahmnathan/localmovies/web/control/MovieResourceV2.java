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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class MovieResourceV2 {

    @Value("${media.path}")
    private String[] mediaPaths;
    private final MovieInfoFacade movieInfoFacade;
    private final MoviePushNotificationHandler notificationHandler;
    private final Logger logger = Logger.getLogger(com.github.rahmnathan.localmovies.web.control.MovieResource.class.getName());

    @Autowired
    public MovieResourceV2(MovieInfoFacade movieInfoControl, MoviePushNotificationHandler notificationHandler){
        this.movieInfoFacade = movieInfoControl;
        this.notificationHandler = notificationHandler;
    }

    @RequestMapping(value = "/v2/titlerequest", method = RequestMethod.POST, produces="application/json", consumes = "application/json")
    public List<MediaFile> titleRequest(@RequestBody MovieInfoRequest movieInfoRequest, HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        MDC.put("Client-Address", request.getRemoteAddr());
        logger.info("Received request -> " + movieInfoRequest.toString());

        if(movieInfoRequest.getPushToken() != null && movieInfoRequest.getDeviceId() != null){
            AndroidPushClient pushClient = new AndroidPushClient(movieInfoRequest.getDeviceId(), movieInfoRequest.getPushToken());
            notificationHandler.addPushToken(pushClient);
        }

        // Using file-system specific file separator
        String path = movieInfoRequest.getPath().replace("/", File.separator);
        logger.log(Level.INFO, String.format("Received request for - %s page - %s resultsPerPage - %s",
                movieInfoRequest.getPath(), movieInfoRequest.getPage(), movieInfoRequest.getResultsPerPage()));

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

        MDC.clear();
        return movieInfoList;
    }

    private void movieInfoCount(String path, HttpServletResponse response, HttpServletRequest request){
        MDC.put("Client-Address", request.getRemoteAddr());

        // Using file-system specific file separator
        path = path.replace("/", File.separator);
        logger.log(Level.INFO, "Received request for count for - " + path);

        int count = movieInfoFacade.loadMovieListLength(path);
        logger.log(Level.INFO, "Returning count of - " + count);
        response.setHeader("Count", String.valueOf(count));
        MDC.clear();
    }
}