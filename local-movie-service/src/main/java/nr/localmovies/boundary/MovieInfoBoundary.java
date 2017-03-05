package nr.localmovies.boundary;

import nr.localmovies.control.MovieInfoControl;
import nr.localmovies.exception.EmptyDirectoryException;
import nr.localmovies.exception.LocalMovieException;
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

    public List<MovieInfo> loadMovieList(String directoryPath) throws EmptyDirectoryException, UnauthorizedFolderException {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        for (File videoFile : listFiles(directoryPath)) {
            movieInfoList.add(movieInfoControl.loadMovieInfoFromCache(videoFile.getAbsolutePath()));
        }
        return movieInfoList;
    }

    public MovieInfo loadSingleMovie(String filePath) throws ExecutionException {
        if (!filePath.contains("LocalMedia")) {
            return null;
        }

        return movieInfoControl.loadMovieInfoFromCache(filePath);
    }

    private File[] listFiles(String directoryPath) throws EmptyDirectoryException, UnauthorizedFolderException {
        if(!directoryPath.toLowerCase().contains("localmedia")){
            throw new UnauthorizedFolderException();
        }
        File[] files = new File(directoryPath).listFiles();
        if(files == null || files.length == 0){
            throw new EmptyDirectoryException();
        }

        return files;
    }

    public List<MovieInfo> returnErrorList(LocalMovieException e){
        List<MovieInfo> movieInfoList = new ArrayList<>();
        switch (e.getErrorEnum()){
            case EMPTY_DIRECTORY:
                movieInfoList.add(MovieInfo.Builder.newInstance()
                        .setTitle("Path must contain 'LocalMedia' directory and not be empty")
                        .build());
                break;
            case UNAUTHORIZED_FOLDER:
                movieInfoList.add(MovieInfo.Builder.newInstance()
                        .setTitle("Path must contain 'LocalMedia' directory and not be empty")
                        .build());
                break;
        }
        return movieInfoList;
    }
}
