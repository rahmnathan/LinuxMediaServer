package nr.localmovies.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Component
public class MovieInfoControl {
    private final MovieInfoRepository repository;
    private final IMovieInfoProvider movieInfoProvider;
    private final Logger logger = Logger.getLogger(MovieInfoControl.class.getName());

    private final LoadingCache<String, MovieInfo> movieInfoCache =
            CacheBuilder.newBuilder()
                    .maximumSize(500)
                    .build(
                            new CacheLoader<String, MovieInfo>() {
                                @Override
                                public MovieInfo load(String currentPath) {
                                    if(repository.exists(currentPath)){
                                        return loadMovieInfoFromDatabase(currentPath);
                                    } else if (currentPath.split("LocalMedia")[1].split("/").length == 3){
                                        return loadMovieInfoFromOmdb(currentPath);
                                    } else {
                                        return loadSeriesParentInfo(currentPath);
                                    }
                                }
                            });

    @Autowired
    public MovieInfoControl(MovieInfoRepository repository, IMovieInfoProvider movieInfoProvider){
        this.repository = repository;
        this.movieInfoProvider = movieInfoProvider;
    }

    public MovieInfo loadMovieInfoFromCache(String path){
        try {
            return movieInfoCache.get(path);
        } catch (ExecutionException e){
            e.printStackTrace();
            return MovieInfo.Builder.newInstance().build();
        }
    }

    private MovieInfo loadMovieInfoFromDatabase(String path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path);
    }

    private MovieInfo loadMovieInfoFromOmdb(String path) {
        logger.info("Getting from OMDB - " + path);
        String[] pathArray = path.split("/");
        String title = pathArray[pathArray.length - 1];
        MovieInfo movieInfo = movieInfoProvider.loadMovieInfo(title);
        movieInfo.setPath(path);
        repository.save(movieInfo);
        return movieInfo;
    }

    private MovieInfo loadSeriesParentInfo(String path) {
        logger.info("Getting info from parent - " + path);
        String[] pathArray = path.split("LocalMedia")[1].split("/");
        int depth = 0;
        if (pathArray.length == 4 || pathArray.length == 5)
            depth = pathArray.length - 3;

        StringBuilder sb = new StringBuilder();
        String[] directoryArray = path.split("/");
        for (int i = 0; i < directoryArray.length - depth; i++) {
            sb.append(directoryArray[i]);
            sb.append("/");
        }
        String parentPath = sb.toString().substring(0, sb.length() - 1);
        MovieInfo movieInfo = loadMovieInfoFromDatabase(parentPath);
        MovieInfo.Builder movieInfoBuilder = MovieInfo.Builder.newInstance();

        if(movieInfo == null)
            return movieInfoBuilder.build();

        return movieInfoBuilder
                .setTitle(pathArray[pathArray.length - 1])
                .setReleaseYear(movieInfo.getReleaseYear())
                .setMetaRating(movieInfo.getMetaRating())
                .setIMDBRating(movieInfo.getIMDBRating())
                .setImage(movieInfo.getImage())
                .build();
    }
}