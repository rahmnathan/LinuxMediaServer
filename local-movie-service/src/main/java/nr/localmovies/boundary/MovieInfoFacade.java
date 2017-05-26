package nr.localmovies.boundary;

import nr.localmovies.control.FileListProvider;
import nr.localmovies.control.MovieInfoControl;
import nr.localmovies.data.MovieOrder;
import nr.localmovies.data.MovieSearchCriteria;
import nr.localmovies.directory.monitor.DirectoryMonitor;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieInfoFacade {
    @Value("${media.path}")
    private String mediaPath;
    private final MovieInfoControl movieInfoControl;
    private final DirectoryMonitor directoryMonitor;
    private final FileListProvider fileListProvider;

    @Autowired
    public MovieInfoFacade(MovieInfoControl movieInfoControl, DirectoryMonitor directoryMonitor, FileListProvider fileListProvider){
        this.movieInfoControl = movieInfoControl;
        this.directoryMonitor = directoryMonitor;
        this.fileListProvider = fileListProvider;
    }

    @PostConstruct
    public void startDirectoryMonitor() {
        directoryMonitor.startRecursiveWatcher(mediaPath);
    }

    public int loadMovieListLength(String directoryPath){
        return fileListProvider.listFiles(directoryPath).length;
    }

    public List<MovieInfo> loadMovieList(MovieSearchCriteria searchCriteria) {
        List<File> files = Arrays.asList(fileListProvider.listFiles(searchCriteria.getPath()));
        return files.parallelStream()
                .sorted()
                .skip(searchCriteria.getPage() * searchCriteria.getItemsPerPage())
                .limit(searchCriteria.getItemsPerPage())
                .map(file -> movieInfoControl.loadMovieInfoFromCache(file.getAbsolutePath().substring(mediaPath.length())))
                .collect(Collectors.toList());
    }

    public MovieInfo loadSingleMovie(String filePath) {
        return movieInfoControl.loadMovieInfoFromCache(filePath);
    }

    public List<MovieInfo> sortMovieInfoList(List<MovieInfo> movieInfoList, String orderString){
        MovieOrder order = MovieOrder.valueOf(orderString);
        switch (order){
            case DATE_ADDED:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Long.valueOf(movie2.getDateCreated()).compareTo(movie1.getDateCreated()))
                        .collect(Collectors.toList());
            case MOST_VIEWS:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Integer.valueOf(movie2.getViews()).compareTo(movie1.getViews()))
                        .collect(Collectors.toList());
            case RELEASE_YEAR:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Long.valueOf(movie2.getReleaseYear()).compareTo(Long.valueOf(movie1.getReleaseYear())))
                        .collect(Collectors.toList());
            case RATING:
                return movieInfoList.parallelStream()
                        .sorted((movie1, movie2) -> Double.valueOf(movie2.getIMDBRating()).compareTo(Double.valueOf(movie1.getIMDBRating())))
                        .collect(Collectors.toList());
        }
        return movieInfoList;
    }
}
