package nr.localmovies.restserver;

import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
public class RestListener {

    @Autowired
    private MovieInfoBoundary movieInfoBoundary;
    private static Logger logger = Logger.getLogger(RestListener.class.getName());

    /**
     *
     * @param currentPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public List<MovieInfo> titlerequest(@RequestParam(value = "path") String currentPath) {
        logger.info("Received request for path:" + currentPath);
        File[] fileArray = new File(currentPath).listFiles();
        List<MovieInfo> movieInfoList = new ArrayList<>();
        for (File videoFile : fileArray) {
            try {
                movieInfoList.add(movieInfoBoundary.MOVIE_INFO_LOADER.get(videoFile.getAbsolutePath()));
            } catch (ExecutionException e) {
                logger.info(e.getMessage());
            }
        }
        return movieInfoList;
    }

    /**
     * This endpoint clears the cache of all movie info and retrieves updated info
     */
    @RequestMapping("/refresh")
    public void refresh(){
        movieInfoBoundary.MOVIE_INFO_LOADER.invalidateAll();
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
        MovieInfo info = movieInfoBoundary.MOVIE_INFO_LOADER.get(path);
        return Base64.getDecoder().decode(info.getImage());
    }
}
