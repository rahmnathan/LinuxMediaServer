package nr.localmovies.restserver;

import nr.linuxmedieserver.directoryexplorer.DirectoryExplorer;
import nr.localmovies.omdbmovieinfoprovider.OMDBIMovieInfoProvider;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.MovieInfoEntity;
import nr.localmovies.movieinfoapi.MovieInfoRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
class MovieInfoRetriever {

    @Autowired
    private IMovieInfoProvider I_MOVIE_INFO_PROVIDER;

    @Autowired
    private DirectoryExplorer directoryExplorer;

    @Autowired
    private MovieInfoRepository repository;

    List<MovieInfo> loadMovieInfo(String path){
        ObjectMapper mapper = new ObjectMapper();
        String[] currentPathArray = path.toLowerCase().split("localmedia")[1].split("/");
        if (repository.exists(path)) {
            try {
                return mapper.readValue(repository.findOne(path).getData(), new TypeReference<List<MovieInfo>>() {
                });
            } catch (IOException e){
                e.printStackTrace();
            }
        } else if(currentPathArray.length == 2) {
            try {
                List<MovieInfo> movieInfoList = I_MOVIE_INFO_PROVIDER.getMovieInfo(directoryExplorer.getTitleList(path), path);
                repository.save(new MovieInfoEntity(path, mapper.writeValueAsString(movieInfoList)));

                return movieInfoList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int depth = 0;
            String title = currentPathArray[2];
            if(currentPathArray.length == 3)
                depth = 1;
            else if (currentPathArray.length == 4)
                depth = 2;

            String imagePath = "";
            for(int i = 0; i < path.split("/").length - depth; i++){
                imagePath += path.split("/")[i] + "/";
            }
            String image = "";
            String MetaRating = "";
            String IMDBRating = "";
            String year = "";
            try {
                for (MovieInfo info : (List<MovieInfo>) mapper.readValue(repository.findOne(imagePath).getData(), new TypeReference<List<MovieInfo>>() {})) {
                    if (info.getTitle().toLowerCase().equals(title.toLowerCase())) {
                        image = info.getImage();
                        MetaRating = info.getMetaRating();
                        IMDBRating = info.getIMDBRating();
                        year = info.getReleaseYear();
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            List<String> titleList = directoryExplorer.getTitleList(path);
            List<MovieInfo> movieInfoList = new ArrayList<>();
            for(String title1 : titleList){
                MovieInfo info = new MovieInfo();
                info.setTitle(title1);
                info.setImage(image);
                info.setIMDBRating(IMDBRating);
                info.setMetaRating(MetaRating);
                info.setReleaseYear(year);
                movieInfoList.add(info);
            }
            return movieInfoList;
        }
        return null;
    }
}
