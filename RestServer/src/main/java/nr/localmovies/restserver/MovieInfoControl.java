package nr.localmovies.restserver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.MovieInfoEntity;
import nr.localmovies.movieinfoapi.MovieInfoRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MovieInfoControl {
    final LoadingCache<String, MovieInfo> MOVIE_INFO_LOADER =
            CacheBuilder.newBuilder()
                    .maximumSize(250)
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
    private MovieInfoRepository repository;
    @Autowired
    private IMovieInfoProvider I_MOVIE_INFO_PROVIDER;
    private ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = Logger.getLogger(RestListener.class.getName());

    private MovieInfo getFromDatabase(String path){
        try {
            return mapper.readValue(repository.findOne(path).getData(), MovieInfo.class);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    private MovieInfo getFromOMDB(String path){
        try {
            String[] splitPath = path.split("/");
            String title = splitPath[splitPath.length - 1];
            MovieInfo movieInfo = I_MOVIE_INFO_PROVIDER.getMovieInfo(title);
            repository.save(new MovieInfoEntity(path, mapper.writeValueAsString(movieInfo)));
            return movieInfo;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    private MovieInfo getParentInfo(String path) {
        String[] currentPathArray = path.split("LocalMedia")[1].split("/");
        int depth = 0;
        if (currentPathArray.length == 4)
            depth = 1;
        else if (currentPathArray.length == 5)
            depth = 2;

        StringBuilder stringBuilder = new StringBuilder();
        String[] directoryArray = path.split("/");
        for (int i = 0; i < directoryArray.length - depth; i++) {
            stringBuilder.append(directoryArray[i]);
            stringBuilder.append("/");
        }
        String parentPath = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
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