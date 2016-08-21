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
    public List<String> titlerequest(@RequestParam(value = "path", defaultValue = "/Movies") String currentPath,
                                     @RequestParam(value = "phoneName") String phoneName,
                                     @RequestParam(value = "phoneIP") String phoneIP,
                                     @RequestParam(value = "casting") boolean casting,
                                     @RequestParam(value = "computerIP") String computerIP,
                                     @RequestParam(value = "chromeIP") String chromeIP) {

        if (!casting) {
            return directoryExplorer.getTitleList(currentPath);
        } else {
            new MoviePlayer(new DeviceConnection(chromeIP, phoneIP, phoneName, currentPath, computerIP)).run();
            return null;
        }
    }
}
