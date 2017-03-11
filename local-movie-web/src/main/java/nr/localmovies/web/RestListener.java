package nr.localmovies.web;

import nr.localmovies.boundary.MovieInfoBoundary;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class RestListener {
    private final MovieInfoBoundary movieInfoBoundary;
    private final MultipartFileSender fileSender;
    private final Logger logger = Logger.getLogger(RestListener.class.getName());

    @Autowired
    public RestListener(MovieInfoBoundary movieInfoControl, MultipartFileSender fileSender){
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
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws ExecutionException {

        logger.log(Level.INFO, "Received request for - " + directoryPath + " page - " + page + " itemsPerPage - "
                + itemsPerPage + " from " + request.getRemoteAddr());
        response.addHeader("Access-Control-Allow-Origin", "*");
        if(0 == page) {
            int count = movieInfoBoundary.loadMovieListLength(directoryPath);
            logger.log(Level.INFO, "Returning count of - " + count);
            response.addHeader("Count", String.valueOf(count));
        }

        return movieInfoBoundary.loadMovieList(directoryPath, page, itemsPerPage);
    }

    /**
     *
     * @param path - Path to directory you wish to count files from
     * @return
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
    @RequestMapping("/video.mp4")
    public void streamVideo(@RequestParam("path") String moviePath, HttpServletResponse response,
                            HttpServletRequest request) throws IOException {

        if(moviePath.toLowerCase().contains("localmedia")) {
            logger.info("Streaming - " + moviePath + " to " + request.getRemoteAddr());
            response.addHeader("Access-Control-Allow-Origin", "*");
            fileSender.serveResource(Paths.get(moviePath), request, response);
        }
    }

    /**
     * @param moviePath - Path to video file
     * @return - Poster image for specified video file
     * throws Exception
     */
    @RequestMapping("/poster")
    public byte[] servePoster(@RequestParam("path") String moviePath, HttpServletResponse response,
                              HttpServletRequest request) throws ExecutionException {

        logger.info("Streaming poster " + moviePath + " to " + request.getRemoteAddr());
        response.addHeader("Access-Control-Allow-Origin", "*");
        String image = null;

        if(moviePath.toLowerCase().contains("localmedia"))
            image = movieInfoBoundary.loadSingleMovie(moviePath).getImage();
        if(image == null)
            return new byte[0];

        return Base64.getDecoder().decode(image);
    }
}