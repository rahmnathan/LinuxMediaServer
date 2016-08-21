package networking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryExplorer {

    public List<String> getTitleList(String currentPath){

        File[] movieList = new File(currentPath).listFiles();
        List<String> titles = new ArrayList<>();

        try {
            for (File file : movieList) {
                titles.add(file.getName());
            }
        } catch(NullPointerException e){
            titles.add("No files found in this directory");
        }
    return titles;
    }
}