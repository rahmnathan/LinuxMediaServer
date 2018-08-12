package com.github.rahmnathan.localmovie.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.rahmnathan.omdb.data.Movie;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Calendar;

@Entity(name = "movies")
public class MediaFile {

    @Id
    private String path;
    private String fileName;
    private long created;
    private int views;
    private Movie movie;
    @Version
    @JsonIgnore
    private long version;

    private MediaFile(String path, Movie movie, int views, String fileName) {
        this.path = path;
        this.movie = movie;
        this.fileName = fileName;
        this.views = views;
        created = Calendar.getInstance().getTimeInMillis();
    }

    public MediaFile(){
        // Default constructor
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public void addView(){
        views++;
    }

    public int getViews() {
        return views;
    }

    public long getCreated() {
        return created;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie){
        this.movie = movie;
    }

    @Override
    public String toString(){
        return movie.getTitle();
    }

    public static class Builder {
        private String fileName;
        private String path;
        private int views;
        private Movie movieInfo;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setMovie(Movie movie) {
            this.movieInfo = movie;
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

        public MediaFile build(){
            return new MediaFile(path, movieInfo, views, fileName);
        }

        public static MediaFile copyWithNewTitle(MediaFile mediaFile, String fileName, String title, String path){
            if(mediaFile == null)
                return Builder.newInstance()
                        .setFileName(fileName)
                        .setPath(path)
                        .setMovie(Movie.Builder.newInstance().setTitle(title).build())
                        .build();

            return Builder.newInstance()
                    .setFileName(fileName)
                    .setMovie(Movie.Builder.copyWithNewTitle(mediaFile.getMovie(), title))
                    .setPath(path)
                    .build();
        }

        public static MediaFile copyWithNoImage(MediaFile mediaFile){
            if(mediaFile == null)
                return Builder.newInstance().build();

            return Builder.newInstance()
                    .setFileName(mediaFile.getFileName())
                    .setMovie(Movie.Builder.copyWithNoImage(mediaFile.getMovie()))
                    .setViews(mediaFile.getViews())
                    .setPath(mediaFile.getPath())
                    .build();
        }
    }
}