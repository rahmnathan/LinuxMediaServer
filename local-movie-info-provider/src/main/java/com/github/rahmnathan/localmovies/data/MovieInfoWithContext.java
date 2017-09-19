package com.github.rahmnathan.localmovies.data;

import com.github.rahmnathan.movie.info.data.MovieInfo;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "movies")
public class MovieInfoWithContext {

    @Id
    private String path;
    private long created;
    private int views;
    private MovieInfo movieInfo;

    private MovieInfoWithContext(String path, MovieInfo movieInfo, int views) {
        this.path = path;
        this.movieInfo = movieInfo;
        this.views = views;
        created = Calendar.getInstance().getTimeInMillis();
    }

    public MovieInfoWithContext(){
        // Default constructor
    }

    public void addView(){
        views++;
    }

    public int getViews() {
        return views;
    }

    public long getDateCreated() {
        return created;
    }

    @Override
    public String toString(){
        return movieInfo.getTitle();
    }

    public static class Builder {
        private String path;
        private int views;
        private MovieInfo movieInfo;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setMovieInfo(MovieInfo movieInfo) {
            this.movieInfo = movieInfo;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setViews(int views) {
            this.views = views;
            return this;
        }

        public MovieInfoWithContext build(){
            return new MovieInfoWithContext(path, movieInfo, views);
        }

        public static MovieInfoWithContext copyWithNewTitle(MovieInfoWithContext movieInfoWithContext, String title){
            if(movieInfoWithContext == null)
                return Builder.newInstance().setTitle(title).build();

            return Builder.newInstance()
                    .setTitle(title)
                    .setReleaseYear(movieInfoWithContext.getReleaseYear())
                    .setMetaRating(movieInfoWithContext.getMetaRating())
                    .setIMDBRating(movieInfoWithContext.getIMDBRating())
                    .setPath(movieInfoWithContext.getPath())
                    .setImage(movieInfoWithContext.getImage())
                    .setGenre(movieInfoWithContext.getGenre())
                    .build();
        }

        public static MovieInfoWithContext copyWithNoImage(MovieInfoWithContext movieInfoWithContext){
            if(movieInfoWithContext == null)
                return Builder.newInstance().build();

            Builder builder = Builder.newInstance()
                    .setTitle(movieInfoWithContext.getTitle())
                    .setReleaseYear(movieInfoWithContext.getReleaseYear())
                    .setMetaRating(movieInfoWithContext.getMetaRating())
                    .setIMDBRating(movieInfoWithContext.getIMDBRating())
                    .setGenre(movieInfoWithContext.getGenre())
                    .setPath(movieInfoWithContext.getPath());

            if(movieInfoWithContext.getImage() == null || movieInfoWithContext.getImage().equals("")){
                builder.setImage("noImage");
            } else {
                builder.setImage("");
            }

            return builder.build();
        }
    }
}