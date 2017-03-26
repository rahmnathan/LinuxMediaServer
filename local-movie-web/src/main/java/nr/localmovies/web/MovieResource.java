package nr.localmovies.web;

import nr.localmovies.boundary.MovieInfoBoundary;
import nr.localmovies.data.MovieSearchCriteria;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class MovieResource {
    private final MovieInfoBoundary movieInfoBoundary;
    private final FileSender fileSender;
    private final Logger logger = Logger.getLogger(MovieResource.class.getName());

    @Autowired
    public MovieResource(MovieInfoBoundary movieInfoControl, FileSender fileSender){
        this.movieInfoBoundary = movieInfoControl;
        this.fileSender = fileSender;
    }

    /**
     * @param directoryPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public List<MovieInfo> titleRequest(@RequestParam(value = "path") String directoryPath,
                                        @RequestParam(value =  "page", required = false) Integer page,
                                        @RequestParam(value = "resultsPerPage", required = false) Integer itemsPerPage,
                                        HttpServletRequest request, HttpServletResponse response) throws ExecutionException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.log(Level.INFO, "Received request for - " + directoryPath + " page - " + page + " itemsPerPage - "
                + itemsPerPage + " from " + request.getRemoteAddr());

        MovieSearchCriteria searchCriteria = MovieSearchCriteria.Builder.newInstance()
                .setItemsPerPage(itemsPerPage)
                .setPage(page)
                .setPath(directoryPath)
                .build();

        if(searchCriteria.getPage() == 0)
            movieInfoCount(directoryPath, response, request);

        return movieInfoBoundary.loadMovieList(searchCriteria);
    }

    /**
     * @param path - Path to directory you wish to count files from
     */
    @RequestMapping(value = "/movieinfocount")
    public void movieInfoCount(@RequestParam(value = "path") String path, HttpServletResponse response, HttpServletRequest request){
        logger.log(Level.INFO, "Received request for count for - " + path + " from - " + request.getRemoteAddr());
        int count = movieInfoBoundary.loadMovieListLength(path);
        logger.log(Level.INFO, "Returning count of - " + count);
        response.setHeader("Count", String.valueOf(count));
    }

    /**
     * @param moviePath - Path to video file to stream
     * throws Exception
     */
    @RequestMapping(value = "/video.mp4", produces = "video/mp4")
    public void streamVideo(@RequestParam("path") String moviePath, HttpServletResponse response,
                            HttpServletRequest request) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.info("Streaming - " + moviePath + " to " + request.getRemoteAddr());
        if(moviePath.toLowerCase().contains("localmedia"))
            fileSender.serveResource(Paths.get(moviePath), request, response);
    }

    /**
     * @param moviePath - Path to video file
     * @return - Poster image for specified video file
     * throws Exception
     */
    @RequestMapping("/poster")
    public byte[] servePoster(@RequestParam("path") String moviePath, HttpServletResponse response,
                              HttpServletRequest request) throws ExecutionException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.info("Streaming poster " + moviePath + " to " + request.getRemoteAddr());

        String image = movieInfoBoundary.loadSingleMovie(moviePath).getImage();
        if(image == null)
            return new byte[0];

        return Base64.getDecoder().decode(image);
    }
}