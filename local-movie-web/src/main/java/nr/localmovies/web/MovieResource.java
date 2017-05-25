package nr.localmovies.web;

import nr.localmovies.boundary.MovieInfoFacade;
import nr.localmovies.data.MovieOrder;
import nr.localmovies.data.MovieSearchCriteria;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class MovieResource {

    @Value("${media.path}")
    private String mediaPath;
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
                                        HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        MDC.put("Client-Address", request.getRemoteAddr());
        String absolutePath = mediaPath + path;
        logger.log(Level.INFO, String.format("Received request for - %s page - %s itemsPerPage - %s",
                absolutePath, page, itemsPerPage));

        MovieSearchCriteria searchCriteria = MovieSearchCriteria.Builder.newInstance()
                .setItemsPerPage(itemsPerPage)
                .setPage(page)
                .setPath(absolutePath)
                .build();

        if(searchCriteria.getPage() == 0)
            movieInfoCount(path, response, request);

        List<MovieInfo> movieInfoList = movieInfoFacade.loadMovieList(searchCriteria);
        if(orderString != null)
            movieInfoList = movieInfoFacade.sortMovieInfoList(movieInfoList, orderString);

        MDC.clear();
        return movieInfoList;
    }

    /**
     * @param path - Path to directory you wish to count files from
     */
    @RequestMapping(value = "/movieinfocount")
    public void movieInfoCount(@RequestParam(value = "path") String path, HttpServletResponse response, HttpServletRequest request){
        MDC.put("Client-Address", request.getRemoteAddr());
        path = mediaPath + path;
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
        path = mediaPath + path;
        logger.info("Streaming - " + path);
        fileSender.serveResource(Paths.get(path), request, response);
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
}