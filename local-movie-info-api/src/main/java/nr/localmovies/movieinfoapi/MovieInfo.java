package nr.localmovies.movieinfoapi;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity(name = "movies")
public class MovieInfo {

    @Id
    private String path;
    private String title;
    private String IMDBRating;
    private String metaRating;

    @Lob
    private String image;
    private String releaseYear;

    private MovieInfo(String title, String IMDBRating, String metaRating, String image, String releaseYear, String path) {
        this.title = title;
        this.IMDBRating = IMDBRating;
        this.metaRating = metaRating;
        this.image = image;
        this.releaseYear = releaseYear;
        this.path = path;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIMDBRating(String IMDBRating) {
        this.IMDBRating = IMDBRating;
    }

    public void setMetaRating(String metaRating) {
        this.metaRating = metaRating;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
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
    }
}