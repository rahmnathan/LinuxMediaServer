package nr.localmovies.omdbmovieinfoprovider;

import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class OMDBIMovieInfoProvider implements IMovieInfoProvider {
    private static final Logger logger = Logger.getLogger(OMDBIMovieInfoProvider.class.getName());
    private OmdbDataProvider dataProvider;
    private JsonToMovieInfoMapper movieInfoMapper;

    @Autowired
    public OMDBIMovieInfoProvider(OmdbDataProvider dataProvider, JsonToMovieInfoMapper movieInfoMapper){
        this.dataProvider = dataProvider;
        this.movieInfoMapper = movieInfoMapper;
    }

    @Override
    public MovieInfo getMovieInfo(String title){
        String fileName = title;
        if(title.contains("."))
            title = title.substring(0, title.length()-4);

        JSONObject jsonMovieInfo = dataProvider.getData(title);
        byte[] poster = null;
        try {
            URL url = new URL(jsonMovieInfo.get("Poster").toString());
            poster = dataProvider.getImage(url);
        }catch (Exception e){
            logger.log(Level.WARNING, "Unable to get poster for movie - " + title);
        }

        return movieInfoMapper.mapOmdbJsonToMovieInfo(jsonMovieInfo, poster, fileName);
    }
}
