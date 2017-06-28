package com.github.rahmnathan.localmovies.imdbapi.info.provider;

import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.logging.Logger;

@Component
class ImdbApiMovieInfoMapper {

    private final Logger logger = Logger.getLogger(ImdbApiMovieInfoMapper.class.getName());

    MovieInfo jsonToMovieInfo(JSONObject movieJson, byte[] poster, String title){
        MovieInfo.Builder builder = MovieInfo.Builder.newInstance()
                .setTitle(title);

        try{
            builder.setImage(Base64.getEncoder().encodeToString(poster));
        } catch (Exception e){
            logger.severe(e.toString());
        }
        try {
            builder.setReleaseYear(movieJson.getString("year"));
        } catch (Exception e){
            logger.severe(e.toString());
        }
        try {
            builder.setIMDBRating(movieJson.getString("rating"));
        } catch (Exception e){
            logger.severe(e.toString());
        }

        return builder.build();
    }
}
