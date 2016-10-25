package nr.localmovies.movieinfoapi;

import java.util.List;

public interface MovieInfoProvider {

    List<MovieInfo> getMovieInfo(List<String> titleList, String currentPath);
}
