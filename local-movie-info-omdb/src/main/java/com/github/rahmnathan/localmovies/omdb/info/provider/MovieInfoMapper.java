package com.github.rahmnathan.localmovies.omdb.info.provider;

import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
class MovieInfoMapper {

    MovieInfo jsonToMovieInfo(JSONObject jsonObject, byte[] poster, String title){
        MovieInfo.Builder movieInfoBuilder = MovieInfo.Builder.newInstance();
        movieInfoBuilder.setTitle(title);

        if(jsonObject.has("imdbRating"))
            movieInfoBuilder.setIMDBRating(jsonObject.getString("imdbRating"));
        if(jsonObject.has("Metascore"))
            movieInfoBuilder.setMetaRating(jsonObject.getString("Metascore"));
        if(jsonObject.has("Year"))
            movieInfoBuilder.setReleaseYear(jsonObject.getString("Year"));
        if(jsonObject.has("Genre"))
            movieInfoBuilder.setGenre(jsonObject.getString("Genre"));

        try {
            movieInfoBuilder.setImage(Base64.getEncoder().encodeToString(poster));
        } catch (Exception e) {
            movieInfoBuilder.setImage(null);
        }

        return movieInfoBuilder.build();
    }
}
