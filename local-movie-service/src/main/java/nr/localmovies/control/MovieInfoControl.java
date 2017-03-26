package nr.localmovies.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import nr.localmovies.data.LocalMediaPath;
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
                                    LocalMediaPath mediaPath = new LocalMediaPath(currentPath);
                                    if(repository.exists(mediaPath.toString())){
                                        return loadMovieInfoFromDatabase(mediaPath);
                                    } else if (mediaPath.isViewingTopLevel()){
                                        return loadMovieInfoFromOmdb(mediaPath);
                                    } else {
                                        return loadSeriesParentInfo(mediaPath);
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

    private MovieInfo loadMovieInfoFromDatabase(LocalMediaPath path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path.toString());
    }

    private MovieInfo loadMovieInfoFromOmdb(LocalMediaPath path) {
        logger.info("Getting from OMDB - " + path);
        MovieInfo movieInfo = movieInfoProvider.loadMovieInfo(path.peekLast());
        movieInfo.setPath(path.toString());
        repository.save(movieInfo);
        return movieInfo;
    }

    private MovieInfo loadSeriesParentInfo(LocalMediaPath path) {
        logger.info("Getting info from parent - " + path);
        MovieInfo movieInfo = loadMovieInfoFromDatabase(path.getParentPath());
        return MovieInfo.Builder.copyWithNewTitle(movieInfo, path.peekLast());
    }
}