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

    public List<MovieInfo> loadMovieInfoList(String directoryPath) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        try {
            for (File videoFile : listFiles(directoryPath)) {
                movieInfoList.add(movieInfoControl.movieInfoCache.get(videoFile.getAbsolutePath()));
            }
        } catch (UnauthorizedFolderException | EmptyDirectoryException | ExecutionException e){
            movieInfoList.add(MovieInfo.Builder.newInstance()
                    .setTitle("Path must contain 'LocalMedia' directory and not be empty").build());
        }
        return movieInfoList;
    }

    public MovieInfo loadMovieInfo(String filePath) throws ExecutionException {
        if (!filePath.contains("LocalMedia"))
            return null;

        return movieInfoControl.movieInfoCache.get(filePath);
    }

    private File[] listFiles(String path) throws UnauthorizedFolderException, EmptyDirectoryException {
        File[] fileArray = new File(path).listFiles();
        if(fileArray == null || fileArray.length == 0){
            throw new EmptyDirectoryException();
        }
        if(!path.contains("LocalMedia")) {
            throw new UnauthorizedFolderException();
        }
        return fileArray;
    }
}
