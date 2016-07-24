package MovieData;

/*
 * @author Nathan Rahm
 * Created 12/2015
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryExplorer {

    private String currentPath;

    public DirectoryExplorer(String currentPath){
        this.currentPath = currentPath;
    }

    public List<String> getTitleList(){

        File[] movieList = new File(currentPath).listFiles();
        List<String> titles = new ArrayList<>();

        for (File file : movieList) {
            titles.add(file.getName());
        }
    return titles;
    }
}