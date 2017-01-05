package nr.localmovies.restserver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
class MovieInfoRetriever {

    @Autowired
    private DatabaseConnector databaseConnector;

    final LoadingCache<String, MovieInfo> MOVIE_INFO_LOADER =
            CacheBuilder.newBuilder()
                    .maximumSize(250)
                    .build(
                            new CacheLoader<String, MovieInfo>() {
                                @Override
                                public MovieInfo load(String currentPath) {
                                    return databaseConnector.retrieveMovieInfo(currentPath);
                                }
                            });

    List<MovieInfo> loadMovieInfo(String path) {
        File[] fileList = new File(path).listFiles();
        List<MovieInfo> movieInfoList = new ArrayList<>();
        System.out.println(path);
        for (File videoFile : fileList) {
            try {
                movieInfoList.add(MOVIE_INFO_LOADER.get(videoFile.getAbsolutePath()));
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return movieInfoList;
    }
}