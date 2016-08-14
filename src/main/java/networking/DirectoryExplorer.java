package networking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class DirectoryExplorer {

    private final String currentPath;

    public DirectoryExplorer(String currentPath){
        this.currentPath = currentPath;
    }

    public List<String> getTitleList(){

        File[] movieList = new File(currentPath).listFiles();
        List<String> titles = new ArrayList<>();

        try {
            for (File file : movieList) {
                titles.add(file.getName() + "CI-Test");
            }
        } catch(NullPointerException e){
            titles.add("No files found in this directory");
        }
    return titles;
    }
}