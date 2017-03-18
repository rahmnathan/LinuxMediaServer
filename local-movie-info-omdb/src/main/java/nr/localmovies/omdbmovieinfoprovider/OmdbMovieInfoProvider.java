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
    private final OmdbRawDataProvider dataProvider;
    private final MovieInfoBuilder movieInfoMapper;

    @Autowired
    public OmdbMovieInfoProvider(OmdbRawDataProvider dataProvider, MovieInfoBuilder movieInfoBuilder){
        this.dataProvider = dataProvider;
        this.movieInfoMapper = movieInfoBuilder;
    }

    @Override
    public MovieInfo loadMovieInfo(String title){
        String fileName = title;
        if(title.contains("."))
            title = title.substring(0, title.length()-4);

        JSONObject jsonMovieInfo = dataProvider.loadMovieInfo(title);
        byte[] poster = loadPoster(jsonMovieInfo);
        return movieInfoMapper.buildMovieInfo(jsonMovieInfo, poster, fileName);
    }

    private byte[] loadPoster(JSONObject jsonMovieInfo){
        byte[] poster;
        try {
            URL url = new URL(jsonMovieInfo.get("Poster").toString());
            poster = scaleImage(dataProvider.loadMoviePoster(url));
        } catch (Exception e){
            poster = new byte[0];
        }
        return poster;
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
