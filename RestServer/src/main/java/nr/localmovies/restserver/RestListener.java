package nr.localmovies.restserver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class RestListener {

    @Autowired
    private MovieInfoRetriever movieInfoRetriever;

    @Autowired
    private MovieInfoRepository repository;

    private final LoadingCache<String, List<MovieInfo>> MOVIE_INFO_LOADER =
            CacheBuilder.newBuilder()
                    .maximumSize(250)
                    .build(
                            new CacheLoader<String, List<MovieInfo>>() {
                                @Override
                                public List<MovieInfo> load(String currentPath) {
                                    return movieInfoRetriever.loadMovieInfo(currentPath);
                                }
                            });

    /**
     *
     * @param currentPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public List<MovieInfo> titlerequest(@RequestParam(value = "path") String currentPath) {
        try {
            return MOVIE_INFO_LOADER.get(currentPath);
        }catch(ExecutionException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This endpoint clears the cache of all movie info and retrieves updated info
     */
    @RequestMapping("/refresh")
    public void refresh(){
        MOVIE_INFO_LOADER.invalidateAll();
        repository.deleteAll();
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
        for(MovieInfo info : MOVIE_INFO_LOADER.get(path)){
            if(info.getTitle().equals(title))
                return Base64.getDecoder().decode(info.getImage());
        }
        return null;
    }
}
