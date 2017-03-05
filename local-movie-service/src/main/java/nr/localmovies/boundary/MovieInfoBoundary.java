package nr.localmovies.boundary;

import nr.localmovies.control.MovieInfoControl;
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
        for (File videoFile : listFiles(directoryPath)) {
            movieInfoList.add(movieInfoControl.loadMovieInfoFromCache(videoFile.getAbsolutePath()));
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
