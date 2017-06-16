package com.github.rahmnathan.localmovies.boundary;

import com.github.rahmnathan.localmovies.control.MovieInfoControl;
import com.github.rahmnathan.localmovies.movieinfoapi.MovieInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovieInfoFacadeTest {

    private List<MovieInfo> movieInfoList = new ArrayList<>();
    private final MovieInfoFacade movieInfoFacade = new MovieInfoFacade(new MovieInfoControl(null, null, null));

    @Before
    public void initializeMovieList() throws Exception {
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            int randomInt = random.nextInt(50);
            movieInfoList.add(MovieInfo.Builder.newInstance()
                    .setImage("")
                    .setIMDBRating(String.valueOf(randomInt))
                    .setMetaRating(String.valueOf(randomInt))
                    .setPath("/test/path")
                    .setTitle("My Fake Title " + randomInt)
                    .setReleaseYear(String.valueOf(1990 + randomInt))
                    .build()
            );
            Thread.sleep(100);
        }

        movieInfoList.get(2).addView();
    }

    @Test
    public void testDateAddedSort(){
        movieInfoList = movieInfoFacade.sortMovieInfoList(movieInfoList, "DATE_ADDED");
        movieInfoList.forEach(movie -> System.out.println(movie.getDateCreated()));
    }

    @Test
    public void testViewSort(){
        movieInfoList = movieInfoFacade.sortMovieInfoList(movieInfoList, "MOST_VIEWS");
        movieInfoList.forEach(movie -> System.out.println(movie.getViews()));
    }

    @Test
    public void testRatingSort(){
        movieInfoList = movieInfoFacade.sortMovieInfoList(movieInfoList, "RATING");
        movieInfoList.forEach(movie -> System.out.println(movie.getIMDBRating()));
    }
}
