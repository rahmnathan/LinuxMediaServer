package nr.localmovies.restserver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MovieInfoControl {
    private MovieInfoRepository repository;
    private IMovieInfoProvider movieInfoProvider;
    private static final Logger logger = Logger.getLogger(RestListener.class.getName());

    final LoadingCache<String, MovieInfo> movieInfoCache =
            CacheBuilder.newBuilder()
                    .maximumSize(400)
                    .build(
                            new CacheLoader<String, MovieInfo>() {
                                @Override
                                public MovieInfo load(String currentPath) {
                                    if(repository.exists(currentPath)){
                                        return getFromDatabase(currentPath);
                                    } else if (currentPath.split("LocalMedia")[1].split("/").length == 3){
                                        return getFromOMDB(currentPath);
                                    } else {
                                        return getParentInfo(currentPath);
                                    }
                                }
                            });

    @Autowired
    public MovieInfoControl(MovieInfoRepository repository, IMovieInfoProvider movieInfoProvider){
        this.repository = repository;
        this.movieInfoProvider = movieInfoProvider;
    }

    private MovieInfo getFromDatabase(String path){
        logger.info("Getting from database - " + path);
        return repository.findOne(path);
    }

    private MovieInfo getFromOMDB(String path){
        logger.info("Getting from OMDB - " + path);
        try {
            String[] splitPath = path.split("/");
            String title = splitPath[splitPath.length - 1];
            MovieInfo movieInfo = movieInfoProvider.getMovieInfo(title);
            movieInfo.setPath(path);
            repository.save(movieInfo);
            return movieInfo;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    private MovieInfo getParentInfo(String path) {
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
        MovieInfo info = getFromDatabase(parentPath);
        MovieInfo.Builder builder = MovieInfo.Builder.newInstance();

        if(info == null)
            return builder.build();

        return builder
                .setTitle(currentPathArray[currentPathArray.length - 1])
                .setReleaseYear(info.getReleaseYear())
                .setMetaRating(info.getMetaRating())
                .setIMDBRating(info.getIMDBRating())
                .setImage(info.getImage())
                .build();
    }
}