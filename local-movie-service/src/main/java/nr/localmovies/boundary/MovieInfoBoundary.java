package nr.localmovies.boundary;

import nr.localmovies.control.MovieInfoControl;
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

    public List<MovieInfo> loadMovieList(String directoryPath, Integer page, Integer itemsPerPage) {
        List<File> files = Arrays.asList(listFiles(directoryPath));
        files = files.parallelStream().sorted().collect(Collectors.toList());
        files = trimListToCurrentPage(page, itemsPerPage, files);

        List<MovieInfo> movieInfoList = new ArrayList<>();
        files.parallelStream()
                .forEachOrdered((file) -> movieInfoList.add(movieInfoControl.loadMovieInfoFromCache(file.getAbsolutePath())));
        return movieInfoList;
    }

    public MovieInfo loadSingleMovie(String filePath) throws ExecutionException {
        if (!filePath.contains("LocalMedia"))
            return MovieInfo.Builder.newInstance().build();

        return movieInfoControl.loadMovieInfoFromCache(filePath);
    }

    private File[] listFiles(String directoryPath) {
        File[] files = new File(directoryPath).listFiles();
        if(!directoryPath.toLowerCase().contains("localmedia") || files == null || files.length == 0)
            files = new File[0];

        return files;
    }

    private List<File> trimListToCurrentPage(Integer page, Integer itemsPerPage, List<File> files) {
        if (page == null || itemsPerPage == null)
            return files;

        int currentPosition = itemsPerPage * page;
        int listEnd = currentPosition + itemsPerPage;
        if (listEnd > files.size())
            listEnd = files.size();
        return files.subList(currentPosition, listEnd);
    }
}
