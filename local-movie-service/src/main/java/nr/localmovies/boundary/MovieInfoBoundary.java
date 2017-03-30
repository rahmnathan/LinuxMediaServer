package nr.localmovies.boundary;

import nr.localmovies.control.FileLister;
import nr.localmovies.control.MovieInfoControl;
import nr.localmovies.data.MovieSearchCriteria;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class MovieInfoBoundary {
    private MovieInfoControl movieInfoControl;
    private FileLister fileLister;

    @Autowired
    public MovieInfoBoundary(MovieInfoControl movieInfoControl, FileLister fileLister){
        this.movieInfoControl = movieInfoControl;
        this.fileLister = fileLister;
    }

    public int loadMovieListLength(String directoryPath){
        return fileLister.listFiles(directoryPath).length;
    }

    public List<MovieInfo> loadMovieList(MovieSearchCriteria searchCriteria) {
        List<File> files = Arrays.asList(fileLister.listFiles(searchCriteria.getPath()));

        return files.parallelStream()
                .sorted()
                .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                .limit(searchCriteria.getItemsPerPage())
                .map(file -> movieInfoControl.loadMovieInfoFromCache(file.getAbsolutePath()))
                .collect(Collectors.toList());
    }

    public MovieInfo loadSingleMovie(String filePath) throws ExecutionException {
        return movieInfoControl.loadMovieInfoFromCache(filePath);
    }
}
