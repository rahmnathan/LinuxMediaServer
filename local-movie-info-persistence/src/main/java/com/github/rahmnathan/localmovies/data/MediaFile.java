package com.github.rahmnathan.localmovies.data;

import com.github.rahmnathan.movie.info.data.MovieInfo;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "movies")
public class MediaFile {

    @Id
    private String path;
    private String fileName;
    private long created;
    private int views;
    private MovieInfo movieInfo;
    @Version
    private long version;

    private MediaFile(String path, MovieInfo movieInfo, int views, String fileName) {
        this.path = path;
        this.movieInfo = movieInfo;
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

    public long getDateCreated() {
        return created;
    }

    public MovieInfo getMovieInfo() {
        return movieInfo;
    }

    public void setMovieInfo(MovieInfo movieInfo){
        this.movieInfo = movieInfo;
    }

    @Override
    public String toString(){
        return movieInfo.getTitle();
    }

    public static class Builder {
        private String fileName;
        private String path;
        private int views;
        private MovieInfo movieInfo;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
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

        public MediaFile build(){
            return new MediaFile(path, movieInfo, views, fileName);
        }

        public static MediaFile copyWithNewTitle(MediaFile mediaFile, String fileName, String title){
            if(mediaFile == null)
                return Builder.newInstance()
                        .setFileName(fileName)
                        .setMovieInfo(MovieInfo.Builder.newInstance().setTitle(title).build())
                        .build();

            return Builder.newInstance()
                    .setFileName(fileName)
                    .setMovieInfo(MovieInfo.Builder.copyWithNewTitle(mediaFile.getMovieInfo(), title))
                    .setPath(mediaFile.getPath())
                    .setViews(mediaFile.getViews())
                    .build();
        }

        public static MediaFile copyWithNoImage(MediaFile mediaFile){
            if(mediaFile == null)
                return Builder.newInstance().build();

            return Builder.newInstance()
                    .setFileName(mediaFile.getFileName())
                    .setMovieInfo(MovieInfo.Builder.copyWithNoImage(mediaFile.getMovieInfo()))
                    .setViews(mediaFile.getViews())
                    .setPath(mediaFile.getPath())
                    .build();
        }
    }
}