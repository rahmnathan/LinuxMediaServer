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

    @RequestMapping("/titlerequest")
    public List<String> titlerequest(@RequestParam(value = "path") String currentPath) {
        try {
            return titles.get(currentPath);
        }catch(ExecutionException e){
            e.printStackTrace();
        }
        return null;
    }

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

    @RequestMapping("/control")
    public void start(@RequestParam("control") String control,
                      @RequestParam("name") String name) {

        Controls keyPress = Controls.valueOf(control);

        KEY_PRESS_EXECUTOR.executeCommand(keyPress, name);
    }

    @RequestMapping("/refresh")
    public void refresh(){
        titles.invalidateAll();
    }
}