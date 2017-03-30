package nr.localmovies.boundary;

import nr.localmovies.control.DirectoryMonitor;
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
    private DirectoryMonitor directoryMonitor;

    @Autowired
    public MovieInfoBoundary(MovieInfoControl movieInfoControl, DirectoryMonitor directoryMonitor){
        this.movieInfoControl = movieInfoControl;
        this.directoryMonitor = directoryMonitor;
    }

    public int loadMovieListLength(String directoryPath){
        return directoryMonitor.listFiles(directoryPath).length;
    }

    public List<MovieInfo> loadMovieList(MovieSearchCriteria searchCriteria) {
        List<File> files = Arrays.asList(directoryMonitor.listFiles(searchCriteria.getPath()));

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
