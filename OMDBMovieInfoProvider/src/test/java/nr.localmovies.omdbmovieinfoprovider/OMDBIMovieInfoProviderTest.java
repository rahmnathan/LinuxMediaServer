package nr.localmovies.omdbmovieinfoprovider;

import nr.localmovies.movieinfoapi.MovieInfo;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class OMDBIMovieInfoProviderTest {

    @Test
    public void JsonToMovieInfoMapperTest(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imdbRating", "10");
        jsonObject.put("Metascore", "10");
        jsonObject.put("Year", "2000");

        MovieInfo movieInfo = new JsonToMovieInfoMapper().mapJsonToMovieInfo(jsonObject, new byte[0], "TestTitle");
        Assert.assertEquals("10", movieInfo.getIMDBRating());
        Assert.assertEquals("10", movieInfo.getMetaRating());
        Assert.assertEquals("2000", movieInfo.getReleaseYear());
        Assert.assertEquals(0, movieInfo.getImage().length());
        Assert.assertEquals("TestTitle", movieInfo.getTitle());
    }
}
