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
import java.util.logging.Level;
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
            return null;
        }
    }

    private MovieInfo loadMovieInfoFromDatabase(String path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path);
    }

    private MovieInfo loadMovieInfoFromOmdb(String path){
        logger.info("Getting from OMDB - " + path);
        try {
            String[] splitPath = path.split("/");
            String title = splitPath[splitPath.length - 1];
            MovieInfo movieInfo = movieInfoProvider.loadMovieInfo(title);
            movieInfo.setPath(path);
            repository.save(movieInfo);
            return movieInfo;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    private MovieInfo loadSeriesParentInfo(String path) {
        logger.info("Getting info from parent - " + path);
        String[] currentPathArray = path.split("LocalMedia")[1].split("/");
        int depth = 0;
        if (currentPathArray.length == 4 || currentPathArray.length == 5)
            depth = currentPathArray.length - 3;

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
                .setTitle(currentPathArray[currentPathArray.length - 1])
                .setReleaseYear(movieInfo.getReleaseYear())
                .setMetaRating(movieInfo.getMetaRating())
                .setIMDBRating(movieInfo.getIMDBRating())
                .setImage(movieInfo.getImage())
                .build();
    }
}