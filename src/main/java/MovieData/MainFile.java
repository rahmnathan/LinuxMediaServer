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
        String[] movieFolderPaths = Arrays.toString(movieList).split(",");
        
        // Cleaning the Paths
        
        for (String path : movieFolderPaths){
            if (path.charAt(path.length()-1)== ']'){
                movieFolders.add(path.substring(1,path.length()-1));
            }
            else {
                movieFolders.add(path.substring(1));
            }
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