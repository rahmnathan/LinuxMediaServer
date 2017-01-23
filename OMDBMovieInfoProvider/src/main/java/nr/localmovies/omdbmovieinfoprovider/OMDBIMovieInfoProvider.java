package nr.localmovies.omdbmovieinfoprovider;

import com.google.common.io.ByteStreams;

import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class OMDBIMovieInfoProvider implements IMovieInfoProvider {

    private static Logger logger = Logger.getLogger(OMDBIMovieInfoProvider.class.getName());

    @Override
    public MovieInfo getMovieInfo(String title){
        return getInfoFromOMDB(title);
    }

    private MovieInfo getInfoFromOMDB(String title) {
        MovieInfo.Builder movieInfoBuilder = MovieInfo.Builder.newInstance();
        movieInfoBuilder.setTitle(title);
        if(title.contains("."))
            title = title.substring(0, title.length()-4);
        JSONObject jsonObject = getData(title);

        if(jsonObject == null)
            return movieInfoBuilder.build();

        try {
            movieInfoBuilder.setImage(Base64.getEncoder().encodeToString(getImage(jsonObject)));
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

    private JSONObject getData(String title) {
        String uri = "http://www.omdbapi.com/?t=";
        try {
            URL url = new URL(uri + title.replace(" ", "%20"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String end = "";
            String string = br.readLine();
            while (!(string == null)) {
                end = end + string;
                string = br.readLine();
            }
            br.close();
            urlConnection.disconnect();

            return new JSONObject(end);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    private byte[] getImage(JSONObject jsonObject) {
        try {
            URL imageURL = new URL(jsonObject.get("Poster").toString());
            InputStream is = imageURL.openConnection().getInputStream();
            return ByteStreams.toByteArray(is);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }
}
