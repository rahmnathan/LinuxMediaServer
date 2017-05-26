package nr.localmovies.movieinfoapi;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "movies")
public class MovieInfo {

    @Id
    private String path;
    @Lob
    private String image;
    private long created;
    private int views;
    private String title;
    private String IMDBRating;
    private String metaRating;
    private String releaseYear;

    private MovieInfo(String title, String IMDBRating, String metaRating, String image, String releaseYear, String path) {
        this.title = title;
        this.IMDBRating = IMDBRating;
        this.metaRating = metaRating;
        this.releaseYear = releaseYear;
        this.image = image;
        this.path = path;
        created = Calendar.getInstance().getTimeInMillis();
    }

    public MovieInfo(){
        // Default constructor
    }

    public String getReleaseYear(){
        return releaseYear;
    }

    public String getTitle(){
        return title;
    }

    public String getIMDBRating(){
        return IMDBRating;
    }

    public String getMetaRating(){
        return metaRating;
    }

    public String getImage() {
        return image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        return title;
    }

    public static class Builder {
        private String title;
        private String IMDBRating;
        private String metaRating;
        private String image;
        private String releaseYear;
        private String path;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setIMDBRating(String IMDBRating) {
            this.IMDBRating = IMDBRating;
            return this;
        }

        public Builder setMetaRating(String metaRating) {
            this.metaRating = metaRating;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Builder setReleaseYear(String releaseYear) {
            this.releaseYear = releaseYear;
            return this;
        }

        public Builder setPath(String path){
            this.path = path;
            return this;
        }

        public MovieInfo build(){
            return new MovieInfo(title, IMDBRating, metaRating, image, releaseYear, path);
        }

        public static MovieInfo copyWithNewTitle(MovieInfo movieInfo, String title){
            if(movieInfo == null)
                return Builder.newInstance().setTitle(title).build();

            return Builder.newInstance()
                    .setTitle(title)
                    .setReleaseYear(movieInfo.getReleaseYear())
                    .setMetaRating(movieInfo.getMetaRating())
                    .setIMDBRating(movieInfo.getIMDBRating())
                    .setImage(movieInfo.getImage())
                    .build();
        }
    }
}