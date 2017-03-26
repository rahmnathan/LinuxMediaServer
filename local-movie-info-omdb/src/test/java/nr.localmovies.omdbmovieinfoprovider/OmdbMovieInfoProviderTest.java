package nr.localmovies.omdbmovieinfoprovider;

import nr.localmovies.movieinfoapi.MovieInfo;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class OmdbMovieInfoProviderTest {

    @Test
    public void jsonToMovieInfoMapperTest(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imdbRating", "10");
        jsonObject.put("Metascore", "10");
        jsonObject.put("Year", "2000");

        MovieInfo movieInfo = new MovieInfoMapper().jsonToMovieInfo(jsonObject, new byte[0], "TestTitle");
        Assert.assertEquals("10", movieInfo.getIMDBRating());
        Assert.assertEquals("10", movieInfo.getMetaRating());
        Assert.assertEquals("2000", movieInfo.getReleaseYear());
        Assert.assertEquals(0, movieInfo.getImage().length());
        Assert.assertEquals("TestTitle", movieInfo.getTitle());
    }

    @Test
    public void omdbMovieInfoProviderTest(){
        OmdbMovieInfoProvider movieInfoProvider = new OmdbMovieInfoProvider(new OmdbRawDataProvider(), new MovieInfoMapper());
        MovieInfo theMatrix = movieInfoProvider.loadMovieInfo("The Matrix");

        Assert.assertEquals("The Matrix", theMatrix.getTitle());
        Assert.assertEquals("73", theMatrix.getMetaRating());
        Assert.assertEquals("8.7", theMatrix.getIMDBRating());
        Assert.assertEquals("1999", theMatrix.getReleaseYear());
        Assert.assertNull(theMatrix.getPath());
    }
}
