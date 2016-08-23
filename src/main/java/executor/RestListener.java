package executor;

import networking.DeviceConnection;
import networking.DirectoryExplorer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import player.MoviePlayer;

import java.util.List;

@RestController
public class RestListener {

    private final DirectoryExplorer directoryExplorer = new DirectoryExplorer();

    @RequestMapping("/titlerequest")
    public List<String> titlerequest(@RequestParam(value = "path", defaultValue = "/Movies") String currentPath) {

        return directoryExplorer.getTitleList(currentPath);
    }

    @RequestMapping("/playmovie")
    public void playMovie(@RequestParam(value = "path", defaultValue = "/Movies") String currentPath,
                          @RequestParam(value = "phoneName") String phoneName,
                          @RequestParam(value = "phoneIP") String phoneIP,
                          @RequestParam(value = "computerIP") String computerIP,
                          @RequestParam(value = "chromeIP") String chromeIP){

        new MoviePlayer(new DeviceConnection(chromeIP, phoneIP, phoneName, currentPath, computerIP)).run();
    }
}