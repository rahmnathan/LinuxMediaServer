package nr.localmovies.movieinfoapi;

import java.util.List;

public interface IMovieInfoProvider {

    List<MovieInfo> getMovieInfo(List<String> titleList, String currentPath);
}
