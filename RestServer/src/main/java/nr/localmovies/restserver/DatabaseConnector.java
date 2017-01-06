package nr.localmovies.restserver;

import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.MovieInfoEntity;
import nr.localmovies.movieinfoapi.MovieInfoRepository;
import nr.localmovies.omdbmovieinfoprovider.OMDBIMovieInfoProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class DatabaseConnector {

    @Autowired
    private MovieInfoRepository repository;

    private IMovieInfoProvider I_MOVIE_INFO_PROVIDER = new OMDBIMovieInfoProvider();
    private static Logger logger = Logger.getLogger(MovieInfoRetriever.class.getName());

    MovieInfo retrieveMovieInfo(String path) {
        String[] currentPathArray;
        try {
            currentPathArray = path.split("LocalMedia")[1].split("/");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Media path must contain 'localmedia' folder - View Docs for details on folder structure");
        }
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(currentPathArray.length);
        if (repository.exists(path)) {
            try {
                return mapper.readValue(repository.findOne(path).getData(), MovieInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (currentPathArray.length == 3) {
            try {
                String[] splitPath = path.split("/");
                MovieInfo movieInfo = I_MOVIE_INFO_PROVIDER.getMovieInfo(splitPath[splitPath.length - 1], path);
                repository.save(new MovieInfoEntity(path, mapper.writeValueAsString(movieInfo)));
                return movieInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int depth = 0;
            if (currentPathArray.length == 4)
                depth = 1;
            else if (currentPathArray.length == 5)
                depth = 2;

            String imagePath = "";
            for (int i = 0; i < path.split("/").length - depth; i++) {
                imagePath += path.split("/")[i] + "/";
            }
            imagePath = imagePath.substring(0, imagePath.length() - 1);
            System.out.println(imagePath);
            String image = "";
            String MetaRating = "";
            String IMDBRating = "";
            String year = "";
            try {
                MovieInfo info = mapper.readValue(repository.findOne(imagePath).getData(), MovieInfo.class);
                image = info.getImage();
                MetaRating = info.getMetaRating();
                IMDBRating = info.getIMDBRating();
                year = info.getReleaseYear();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MovieInfo info = new MovieInfo();
            info.setTitle(currentPathArray[currentPathArray.length - 1]);
            info.setImage(image);
            info.setIMDBRating(IMDBRating);
            info.setMetaRating(MetaRating);
            info.setReleaseYear(year);
            return info;
        }
        return null;
    }
}
