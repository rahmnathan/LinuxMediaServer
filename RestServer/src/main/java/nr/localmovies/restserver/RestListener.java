package nr.localmovies.restserver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

    private static final LoadingCache<String, List<String>> titles =
            CacheBuilder.newBuilder()
                    .maximumSize(100)
                    .build(
                            new CacheLoader<String, List<String>>() {
                                @Override
                                public List<String> load(String currentPath) {
                                    return directoryExplorer.getTitleList(currentPath);
                                }
                            });

    /**
     *
     * @param currentPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping("/titlerequest")
    public List<String> titlerequest(@RequestParam(value = "path") String currentPath) {
        try {
            return titles.get(currentPath);
        }catch(ExecutionException e){
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
        titles.invalidateAll();
    }
}