package nr.localmovies.omdbmovieinfoprovider;

import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import org.imgscalr.Scalr;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class OmdbMovieInfoProvider implements IMovieInfoProvider {
    private static final Logger logger = Logger.getLogger(OmdbMovieInfoProvider.class.getName());
    private final OmdbDataProvider dataProvider;
    private final JsonToMovieInfoMapper movieInfoMapper;

    @Autowired
    public OmdbMovieInfoProvider(OmdbDataProvider dataProvider, JsonToMovieInfoMapper movieInfoMapper){
        this.dataProvider = dataProvider;
        this.movieInfoMapper = movieInfoMapper;
    }

    @Override
    public MovieInfo loadMovieInfo(String title){
        String fileName = title;
        if(title.contains("."))
            title = title.substring(0, title.length()-4);

        JSONObject jsonMovieInfo = dataProvider.loadMovieInfo(title);
        byte[] poster = null;
        try {
            URL url = new URL(jsonMovieInfo.get("Poster").toString());
            poster = dataProvider.loadMoviePoster(url);
            poster = scaleImage(poster);
        } catch (Exception e){
            logger.log(Level.WARNING, "Unable to get poster for movie - " + title);
        }

        return movieInfoMapper.mapOmdbJsonToMovieInfo(jsonMovieInfo, poster, fileName);
    }

    private byte[] scaleImage(byte[] poster) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(poster));
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, 300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        outputStream.flush();
        byte[] resizedPoster = outputStream.toByteArray();
        outputStream.close();
        return resizedPoster;
    }
}
