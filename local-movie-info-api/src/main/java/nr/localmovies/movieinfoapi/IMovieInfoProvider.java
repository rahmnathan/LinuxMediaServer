package nr.localmovies.movieinfoapi;

@FunctionalInterface
public interface IMovieInfoProvider {

    MovieInfo loadMovieInfo(String title);
}
