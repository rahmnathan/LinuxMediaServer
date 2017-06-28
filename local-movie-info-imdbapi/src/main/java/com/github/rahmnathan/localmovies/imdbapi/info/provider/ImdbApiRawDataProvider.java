package com.github.rahmnathan.localmovies.imdbapi.info.provider;

import com.google.common.io.ByteStreams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

@Component
public class ImdbApiRawDataProvider {

    private final Logger logger = Logger.getLogger(ImdbApiRawDataProvider.class.getName());

    JSONObject loadMovieInfo(String title){
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://theimdbapi.org/api/find/movie?title=" + title.replace(" ", "%20"));
            System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e){
            logger.severe(e.toString());
        }

        StringBuilder movieData = new StringBuilder();
        if(connection != null){
            try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                br.lines().forEach(movieData::append);
            } catch (IOException e){
                logger.severe(e.toString());
            }
        }

        JSONArray jsonArray = new JSONArray(movieData.toString());
        return jsonArray.getJSONObject(0);
    }

    byte[] loadMoviePoster(URL posterURL){
        try(InputStream is = posterURL.openConnection().getInputStream()){
            return ByteStreams.toByteArray(is);
        } catch (IOException e){
            logger.info(e.toString());
            return new byte[0];
        }
    }
}
