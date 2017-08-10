package com.github.rahmnathan.localmovies.web;

import com.github.rahmnathan.file.sender.FileSender;
import com.github.rahmnathan.localmovies.boundary.MovieInfoFacade;
import com.github.rahmnathan.localmovies.data.MovieSearchCriteria;
import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class MovieResource {

    @Value("${media.path}")
    private String[] mediaPaths;
    private final MovieInfoFacade movieInfoFacade;
    private final FileSender fileSender;
    private final Logger logger = Logger.getLogger(MovieResource.class.getName());

    @Autowired
    public MovieResource(MovieInfoFacade movieInfoControl, FileSender fileSender){
        this.movieInfoFacade = movieInfoControl;
        this.fileSender = fileSender;
    }

    /**
     * @param path Directory of videos to return
     * @param page Page to return
     * @param itemsPerPage Items to return per page
     * @return List of movie-info json objects
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public List<MovieInfo> titleRequest(@RequestParam(value = "path") String path,
                                        @RequestParam(value =  "page", required = false) Integer page,
                                        @RequestParam(value = "resultsPerPage", required = false) Integer itemsPerPage,
                                        @RequestParam(value = "order", required = false) String orderString,
                                        @RequestParam(value = "client", required = false) String client,
                                        HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        MDC.put("Client-Address", request.getRemoteAddr());
        logger.log(Level.INFO, String.format("Received request for - %s page - %s resultsPerPage - %s", path, page, itemsPerPage));

        MovieSearchCriteria searchCriteria = MovieSearchCriteria.Builder.newInstance()
                .setItemsPerPage(itemsPerPage)
                .setClient(client)
                .setPage(page)
                .setPath(path)
                .setOrder(orderString)
                .build();

        if(searchCriteria.getPage() == 0)
            movieInfoCount(path, response, request);

        List<MovieInfo> movieInfoList = movieInfoFacade.loadMovieList(searchCriteria);

        MDC.clear();
        return movieInfoList;
    }

    /**
     * @param path - Path to directory you wish to count files from
     */
    @RequestMapping(value = "/movieinfocount")
    public void movieInfoCount(@RequestParam(value = "path") String path, HttpServletResponse response, HttpServletRequest request){
        MDC.put("Client-Address", request.getRemoteAddr());
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
    public void streamVideo(@RequestParam("path") String path, HttpServletResponse response,
                            HttpServletRequest request) {
        MDC.put("Client-Address", request.getRemoteAddr());
        response.addHeader("Access-Control-Allow-Origin", "*");
        MovieInfo movie = movieInfoFacade.loadSingleMovie(path);
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
        logger.info("Streaming poster " + path);

        String image = movieInfoFacade.loadSingleMovie(path).getImage();
        if(image == null)
            return new byte[0];

        byte[] poster = Base64.getDecoder().decode(image);
        MDC.clear();
        return poster;
    }

    /**
     * @param date - Date to check if there has been updates since
     * @return - Boolean
     */
    @RequestMapping("/hasUpdates")
    public boolean checkForUpdates(@RequestParam("date") String date, HttpServletResponse response, HttpServletRequest request) {
        MDC.put("Client-Address", request.getRemoteAddr());
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.info("Checking for updates since ->  " + date);
        boolean changes = movieInfoFacade.hasUpdates(date);
        MDC.clear();
        return changes;
    }
}