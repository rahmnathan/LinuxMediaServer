package com.github.rahmnathan.localmovies.imdbapi.info.provider;

import com.github.rahmnathan.localmovies.movieinfoapi.IMovieInfoProvider;
import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ImdbApiMovieInfoProvider implements IMovieInfoProvider {

    private final ImdbApiRawDataProvider dataProvider;
    private final ImdbApiMovieInfoMapper movieInfoMapper;

    @Autowired
    public ImdbApiMovieInfoProvider(ImdbApiRawDataProvider dataProvider, ImdbApiMovieInfoMapper movieInfoMapper) {
        this.dataProvider = dataProvider;
        this.movieInfoMapper = movieInfoMapper;
    }

    @Override
    public MovieInfo loadMovieInfo(String title) {
        String fileName = title;
        if(title.contains("."))
            title = title.substring(0, title.length()-4);

        JSONObject jsonMovieInfo = dataProvider.loadMovieInfo(title);
        byte[] poster = loadPoster(jsonMovieInfo);
        return movieInfoMapper.jsonToMovieInfo(jsonMovieInfo, poster, fileName);
    }

    private byte[] loadPoster(JSONObject jsonMovieInfo) {
        try {
            URL url = new URL(jsonMovieInfo.getJSONObject("poster").get("thumb").toString());
            return dataProvider.loadMoviePoster(url);
        } catch (MalformedURLException | JSONException e) {
            return new byte[0];
        }
    }
}
