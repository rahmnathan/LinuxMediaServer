package nr.localmovies.boundary;

import nr.localmovies.control.MovieInfoControl;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class MovieInfoBoundary {
    private MovieInfoControl movieInfoControl;
    private final int MOVIES_PER_PAGE = 30;

    @Autowired
    public MovieInfoBoundary(MovieInfoControl movieInfoControl){
        this.movieInfoControl = movieInfoControl;
    }

    public int loadMovieListLength(String directoryPath){
        return listFiles(directoryPath).length;
    }

    public List<MovieInfo> loadMovieList(String directoryPath, int page, int itemsPerPage) {
        int listStart = MOVIES_PER_PAGE * page;
        int listEnd = listStart + itemsPerPage;
        List<MovieInfo> movieInfoList = new ArrayList<>();
        List<File> files = Arrays.asList(listFiles(directoryPath));
        Collections.sort(files);
        for (int i = listStart; i < listEnd; i++) {
            if(i >= files.size())
                break;

            movieInfoList.add(movieInfoControl.loadMovieInfoFromCache(files.get(i).getAbsolutePath()));
        }
        return movieInfoList;
    }

    public MovieInfo loadSingleMovie(String filePath) throws ExecutionException {
        if (!filePath.contains("LocalMedia")) {
            return MovieInfo.Builder.newInstance().build();
        }
        return movieInfoControl.loadMovieInfoFromCache(filePath);
    }

    private File[] listFiles(String directoryPath) {
        File[] files = new File(directoryPath).listFiles();
        if(!directoryPath.toLowerCase().contains("localmedia") || files == null || files.length == 0)
            files = new File[0];

        return files;
    }
}
