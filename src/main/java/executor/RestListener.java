package executor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import networking.DeviceConnection;
import networking.DirectoryExplorer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import player.MoviePlayer;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class RestListener {

    private static final DirectoryExplorer directoryExplorer = new DirectoryExplorer();

    public static final LoadingCache<String, List<String>> titles =
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
    public List<String> titlerequest(@RequestParam(value = "path", defaultValue = "/Movies") String currentPath) {

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

        new MoviePlayer(new DeviceConnection(chromeIP, phoneName, currentPath, computerIP)).run();
    }

    @RequestMapping("/refresh")
    public void refresh(){
        titles.invalidateAll();
    }
}