package MovieData;

/*
 * @author Nathan Rahm
 * Created 12/2015
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFile {

    private String currentPath;

    public MainFile (String currentPath){
        this.currentPath = currentPath;
    }

    public List<String> getTitles(){

        File[] movieList = new File(currentPath).listFiles();
        List<String> titles = new ArrayList<>();

        for (File file : movieList) {
            titles.add(file.getName());
        }
    return titles;
    }
}