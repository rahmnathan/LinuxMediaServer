package nr.localmovies.restserver;

import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class RestListener {

    @Autowired
    private MovieInfoControl movieInfoControl;
    private static Logger logger = Logger.getLogger(RestListener.class.getName());

    /**
     *
     * @param currentPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public List<MovieInfo> titlerequest(@RequestParam(value = "path") String currentPath) {
        if(!currentPath.contains("LocalMedia")) {
            logger.severe("Path must contain 'LocalMedia' folder");
            return null;
        }
        File[] fileArray = new File(currentPath).listFiles();
        List<MovieInfo> movieInfoList = new ArrayList<>();
        if(fileArray == null || fileArray.length == 0){
            movieInfoList.add(MovieInfo.Builder.newInstance()
                    .setTitle("No Files found in this directory")
                    .build());
            return movieInfoList;
        } else {
            for (File videoFile : fileArray) {
                try {
                    movieInfoList.add(movieInfoControl.MOVIE_INFO_LOADER.get(videoFile.getAbsolutePath()));
                } catch (ExecutionException e) {
                    logger.log(Level.SEVERE, e.toString(), e);
                }
            }
            return movieInfoList;
        }
    }

    /**
     * This endpoint clears the cache of all movie info
     */
    @RequestMapping("/refresh")
    public void refresh(){
        movieInfoControl.MOVIE_INFO_LOADER.invalidateAll();
    }

    /**
     *
     * @param path - Path to video file to stream
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
     * @param path - Path to video file
     * @return - Poster image for specified video file
     * @throws Exception
     */
    @RequestMapping("/poster")
    public byte[] servePoster(@RequestParam("path") String path) throws ExecutionException {
        MovieInfo info = movieInfoControl.MOVIE_INFO_LOADER.get(path);
        return Base64.getDecoder().decode(info.getImage());
    }
}
