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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class OmdbMovieInfoProvider implements IMovieInfoProvider {
    private final OmdbRawDataProvider dataProvider;
    private final MovieInfoMapper movieInfoMapper;

    @Autowired
    public OmdbMovieInfoProvider(OmdbRawDataProvider dataProvider, MovieInfoMapper movieInfoBuilder){
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
        return movieInfoMapper.jsonToMovieInfo(jsonMovieInfo, poster, fileName);
    }

    private byte[] loadPoster(JSONObject jsonMovieInfo){
        URL url;
        try {
            url = new URL(jsonMovieInfo.get("Poster").toString());
        }catch (MalformedURLException e){
            return new byte[0];
        }
        return scaleImage(dataProvider.loadMoviePoster(url));
        }

    private byte[] scaleImage(byte[] poster) {
        if (poster.length == 0)
            return poster;

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(poster));
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, 300);
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e){
            return new byte[0];
        }
    }
}
