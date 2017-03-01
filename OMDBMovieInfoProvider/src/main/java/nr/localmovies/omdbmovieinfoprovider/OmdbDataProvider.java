package nr.localmovies.omdbmovieinfoprovider;

import com.google.common.io.ByteStreams;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
class OmdbDataProvider {
    private static final Logger logger = Logger.getLogger(OmdbDataProvider.class.getName());

    JSONObject getData(String title) {
        String uri = "http://www.omdbapi.com/?t=";
        try {
            URL url = new URL(uri + title.replace(" ", "%20"));
            logger.info("Getting info from OMDB - " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String string = br.readLine();
            while (string != null) {
                stringBuilder.append(string);
                string = br.readLine();
            }
            br.close();
            urlConnection.disconnect();
            return new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    byte[] getImage(URL imageURL) {
        try {
            InputStream is = imageURL.openConnection().getInputStream();
            return ByteStreams.toByteArray(is);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return new byte[0];
    }
}
