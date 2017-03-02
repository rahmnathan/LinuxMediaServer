package nr.localmovies.omdbmovieinfoprovider;

import nr.localmovies.movieinfoapi.MovieInfo;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
class JsonToMovieInfoMapper {
    private static final Logger logger = Logger.getLogger(JsonToMovieInfoMapper.class.getName());

    MovieInfo mapOmdbJsonToMovieInfo(JSONObject jsonObject, byte[] poster, String title){
        MovieInfo.Builder movieInfoBuilder = MovieInfo.Builder.newInstance();
        movieInfoBuilder.setTitle(title);

        try {
            movieInfoBuilder.setImage(Base64.getEncoder().encodeToString(poster));
        } catch (Exception e) {
            movieInfoBuilder.setImage(null);
            logger.log(Level.WARNING, "No image for title - " + title, e);
        }
        try {
            movieInfoBuilder.setIMDBRating(jsonObject.getString("imdbRating"));
        } catch (Exception e) {
            movieInfoBuilder.setIMDBRating("N/A");
            logger.log(Level.WARNING, "No IMDB rating for title - " + title, e);
        }
        try {
            movieInfoBuilder.setMetaRating(jsonObject.getString("Metascore"));
        } catch (Exception e) {
            movieInfoBuilder.setMetaRating("N/A");
            logger.log(Level.WARNING, "No MetaCritic rating for title - " + title, e);
        }
        try {
            movieInfoBuilder.setReleaseYear(jsonObject.getString("Year"));
        } catch (Exception e) {
            movieInfoBuilder.setReleaseYear("N/A");
            logger.log(Level.WARNING, "No release year for title - " + title, e);
        }

        return movieInfoBuilder.build();
    }
}