package nr.localmovies.omdbmovieinfoprovider;

import com.google.common.io.ByteStreams;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
class OmdbRawDataProvider {
    private final Logger logger = Logger.getLogger(OmdbRawDataProvider.class.getName());

    JSONObject loadMovieInfo(String title) {
        String uri = "http://www.omdbapi.com/?t=";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(uri + title.replace(" ", "%20"));
            urlConnection = (HttpURLConnection) url.openConnection();
            logger.info("Getting info from OMDB - " + url.toString());
        } catch (IOException e) {
            logger.fine(e.toString());
        }

        if (urlConnection != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                StringBuilder stringBuilder = new StringBuilder();
                br.lines().forEachOrdered(stringBuilder::append);
                urlConnection.disconnect();
                return new JSONObject(stringBuilder.toString());
            } catch (Exception e) {
                urlConnection.disconnect();
                logger.log(Level.SEVERE, e.toString());
            }
        }
        return new JSONObject();
    }

    byte[] loadMoviePoster(URL imageURL) {
        try(InputStream is = imageURL.openConnection().getInputStream()){
            return ByteStreams.toByteArray(is);
        } catch (IOException e){
            logger.fine(e.toString());
            return new byte[0];
        }
    }
}
