package nr.localmovies.boundary;

import nr.localmovies.control.MovieInfoControl;
import nr.localmovies.data.MovieSearchCriteria;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class MovieInfoBoundary {
    private MovieInfoControl movieInfoControl;

    @Autowired
    public MovieInfoBoundary(MovieInfoControl movieInfoControl){
        this.movieInfoControl = movieInfoControl;
    }

    public int loadMovieListLength(String directoryPath){
        return listFiles(directoryPath).length;
    }

    public List<MovieInfo> loadMovieList(MovieSearchCriteria searchCriteria) {
        List<File> files = Arrays.asList(listFiles(searchCriteria.getPath()));

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

    private File[] listFiles(String directoryPath) {
        File[] files = new File(directoryPath).listFiles();
        if(files == null || files.length == 0)
            files = new File[0];

        return files;
    }
}
