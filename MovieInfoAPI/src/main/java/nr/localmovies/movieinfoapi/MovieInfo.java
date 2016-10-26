package nr.localmovies.movieinfoapi;

public class MovieInfo {

    private String title;
    private String IMDBRating;
    private String metaRating;
    private String image;
    private String releaseYear;

    private MovieInfo(String title, String IMDBRating, String metaRating, String image, String releaseYear) {
        this.title = title;
        this.IMDBRating = IMDBRating;
        this.metaRating = metaRating;
        this.image = image;
        this.releaseYear = releaseYear;
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

        public static Builder newInstace(){
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
        public MovieInfo build(){
            return new MovieInfo(title, IMDBRating, metaRating, image, releaseYear);
        }
    }
}