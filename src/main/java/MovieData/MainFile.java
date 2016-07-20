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
    
    private List<String> movieFolders(){
        
        // Collecting a list of folders that contain movies
        
        List<String> movieFolders = new ArrayList<>();
        File[] movieList = new File(currentPath).listFiles();

        for (File file:movieList){
            movieFolders.add(file.getName());
        }
        return movieFolders;
    }

    public List<String> getTitles(){
        
        // Creating a list of titles from their paths.
        
        List<String> titlesFinal = new ArrayList<>();
        for (String path : movieFolders()) {
            titlesFinal.add(path.substring(currentPath.length()));
        }
    return titlesFinal;
    }
}