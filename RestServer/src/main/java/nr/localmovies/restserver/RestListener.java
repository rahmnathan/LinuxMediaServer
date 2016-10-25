package nr.localmovies.restserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.MovieInfoProvider;
import nr.localmovies.omdbmovieinfoprovider.OMDBMovieInfoProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import nr.linuxmedieserver.device.Device;
import nr.linuxmedieserver.directoryexplorer.DirectoryExplorer;
import nr.linuxmedieserver.keypressexecutor.KeyPressExecutor;
import nr.linuxmedieserver.keypressexecutor.KeyPressExecutor.Controls;
import nr.linuxmedieserver.movieplayer.MoviePlayer;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class RestListener {

    private static final DirectoryExplorer directoryExplorer = new DirectoryExplorer();
    private static final KeyPressExecutor KEY_PRESS_EXECUTOR = new KeyPressExecutor();
    private static final MovieInfoProvider movieInfoProvider = new OMDBMovieInfoProvider();

    private static final LoadingCache<String, List<MovieInfo>> MOVIE_INFO_LOADER =
            CacheBuilder.newBuilder()
                    .maximumSize(250)
                    .build(
                            new CacheLoader<String, List<MovieInfo>>() {
                                @Override
                                public List<MovieInfo> load(String currentPath) {
                                    List<String> currentTitles = directoryExplorer.getTitleList(currentPath);

                                    return movieInfoProvider.getMovieInfo(currentTitles, currentPath);
                                }
                            });

    /**
     *
     * @param currentPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public String titlerequest(@RequestParam(value = "path") String currentPath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(MOVIE_INFO_LOADER.get(currentPath));
        }catch(ExecutionException | JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param currentPath - Path to video you wish to play
     * @param phoneName - Unique name of connection (to avoid connection conflicts)
     * @param computerIP - IP of your server
     * @param chromeIP - IP of the chromecast you wish playback to start on
     */
    @RequestMapping("/playmovie")
    public void playMovie(@RequestParam(value = "path", defaultValue = "/Movies") String currentPath,
                          @RequestParam(value = "phoneName") String phoneName,
                          @RequestParam(value = "computerIP") String computerIP,
                          @RequestParam(value = "chromeIP") String chromeIP){

        Device.Builder builder = Device.Builder.newInstance();

        builder .chromecastIP(chromeIP)
                .computerIP(computerIP)
                .currentPath(currentPath)
                .phoneName(phoneName);

        new MoviePlayer(builder.build()).run();
    }

    /**
     *
     * @param control - The desired control to execute (VOLUME_UP, VOLUME_DOWN, SEEK_FORWARD, SEEK_BACK, PLAY_PAUSE, STOP)
     * @param name - The name of your device that you started playback with
     */
    @RequestMapping("/control")
    public void start(@RequestParam("control") String control,
                      @RequestParam("name") String name) {

        Controls keyPress = Controls.valueOf(control);

        KEY_PRESS_EXECUTOR.executeCommand(keyPress, name);
    }

    /**
     * This endpoint clears the cache of all movie info and retrieves updated info
     */
    @RequestMapping("/refresh")
    public void refresh(){
        MOVIE_INFO_LOADER.invalidateAll();
    }
}