package com.github.rahmnathan.localmovies.imdbapi.info.provider;

import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import com.google.common.io.ByteStreams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class ImdbApiRawDataProviderTest {

    private String rawData;

    @Before
    public void getRawDataTest() throws Exception {
        URL url = new URL("https://theimdbapi.org/api/find/movie?title=transformers&year=2007");
        System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder movieData = new StringBuilder();
        br.lines().forEach(movieData::append);
        br.close();

        rawData = movieData.toString();
    }

    @Test
    public void ImdbApiToMovieInfoTest() throws Exception {
        JSONArray movieInfoArray = new JSONArray(rawData);
        JSONObject firstMatch = (JSONObject) movieInfoArray.get(0);
        System.out.println(firstMatch);

        String posterThumbUrl = firstMatch.getJSONObject("poster")
                .getString("thumb");

        byte[] posterThumbnail = getPosterThumbnail(posterThumbUrl);

        MovieInfo movieInfo = MovieInfo.Builder.newInstance()
                .setTitle("transformers")
                .setIMDBRating(firstMatch.getString("rating"))
                .setReleaseYear(firstMatch.getString("year"))
                .setImage(Base64.getEncoder().encodeToString(posterThumbnail))
                .build();

        System.out.println(movieInfo.getImage());
    }

    private byte[] getPosterThumbnail(String posterUrl) throws Exception {
        URL imageURL = new URL(posterUrl);
        try(InputStream is = imageURL.openConnection().getInputStream()){
            return ByteStreams.toByteArray(is);
        } catch (IOException e){
            System.out.println("Failed to load image");
            return new byte[0];
        }
    }
}
