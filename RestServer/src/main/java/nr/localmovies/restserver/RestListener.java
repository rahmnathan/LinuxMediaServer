package nr.localmovies.restserver;

import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class RestListener {

    @Autowired
    private MovieInfoRetriever infoRetriever;

    private static Logger logger = Logger.getLogger(RestListener.class.getName());

    /**
     *
     * @param currentPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public List<MovieInfo> titlerequest(@RequestParam(value = "path") String currentPath) {
        logger.log(Level.INFO, "Received request for path:" + currentPath);
        return infoRetriever.loadMovieInfo(currentPath);
    }

    /**
     * This endpoint clears the cache of all movie info and retrieves updated info
     */
    @RequestMapping("/refresh")
    public void refresh(){
        infoRetriever.MOVIE_INFO_LOADER.invalidateAll();
    }

    /**
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping("/video.mp4")
    public void streamVideo(HttpServletResponse response, HttpServletRequest request,
                            @RequestParam("path") String path) throws Exception {
        MultipartFileSender.fromFile(new File(path))
                .with(response)
                .with(request)
                .serveResource();
    }

    /**
     *
     * @return - Poster image
     * @throws Exception
     */
    @RequestMapping("/poster")
    public byte[] servePoster(@RequestParam("path") String path, @RequestParam("title") String title) throws Exception {
        MovieInfo info = infoRetriever.MOVIE_INFO_LOADER.get(path);
        return Base64.getDecoder().decode(info.getImage());
    }
}
