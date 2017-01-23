package nr.localmovies.movieinfoapi;

@FunctionalInterface
public interface IMovieInfoProvider {

    MovieInfo getMovieInfo(String title);
}
