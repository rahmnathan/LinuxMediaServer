package nr.localmovies.boundary;

import nr.localmovies.control.MovieInfoControl;
import nr.localmovies.exception.EmptyDirectoryException;
import nr.localmovies.exception.UnauthorizedFolderException;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class MovieInfoBoundary {

    private MovieInfoControl movieInfoControl;

    @Autowired
    public MovieInfoBoundary(MovieInfoControl movieInfoControl){
        this.movieInfoControl = movieInfoControl;
    }

    public List<MovieInfo> loadMovieList(String directoryPath) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        File[] files = new File(directoryPath).listFiles();
        if(files == null || files.length == 0){
            movieInfoList.add(MovieInfo.Builder.newInstance()
                    .setTitle("Path must contain 'LocalMedia' directory and not be empty")
                    .build());
            return movieInfoList;
        }

        for (File videoFile : files) {
            movieInfoList.add(movieInfoControl.loadMovieInfoFromCache(videoFile.getAbsolutePath()));
        }
        return movieInfoList;
    }

    public MovieInfo loadSingleMovie(String filePath) throws ExecutionException {
        if (!filePath.contains("LocalMedia"))
            return null;

        return movieInfoControl.loadMovieInfoFromCache(filePath);
    }
}
