package nr.localmovies.movieinfoapi;

import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class MovieInfoTest {

    @Test
    public void movieInfoEntityTest(){
        EntityManager entityManager = Persistence.createEntityManagerFactory("localmoviestest").createEntityManager();

        MovieInfo originalMovieInfo = MovieInfo.Builder.newInstance()
                .setImage("TestImage")
                .setIMDBRating("5")
                .setMetaRating("5")
                .setTitle("TestTitle")
                .setReleaseYear("ReleaseYear")
                .setPath("TestPath")
                .build();

        entityManager.persist(originalMovieInfo);

        MovieInfo entityFromDatabase = entityManager.find(MovieInfo.class, "TestPath");

        Assert.assertEquals(entityFromDatabase.getPath(), originalMovieInfo.getPath());
        Assert.assertEquals(entityFromDatabase.getTitle(), originalMovieInfo.getTitle());
        Assert.assertEquals(entityFromDatabase.getReleaseYear(), originalMovieInfo.getReleaseYear());
        Assert.assertEquals(entityFromDatabase.getMetaRating(), originalMovieInfo.getMetaRating());
        Assert.assertEquals(entityFromDatabase.getIMDBRating(), originalMovieInfo.getIMDBRating());
        Assert.assertEquals(entityFromDatabase.getImage(), originalMovieInfo.getImage());
    }

    @Test
    public void movieInfoTest(){
        String title = "TestTitle";
        String image = "TestBase64Image";
        String IMDBRating = "TestIMDBRating";
        String metaRating = "TestMetaRating";
        String releaseYear = "TestReleaseYear";

        MovieInfo movieInfo = MovieInfo.Builder.newInstance()
                .setTitle(title)
                .setImage(image)
                .setIMDBRating(IMDBRating)
                .setMetaRating(metaRating)
                .setReleaseYear(releaseYear)
                .build();

        Assert.assertEquals(movieInfo.getTitle(), title);
        Assert.assertEquals(movieInfo.getImage(), image);
        Assert.assertEquals(movieInfo.getIMDBRating(), IMDBRating);
        Assert.assertEquals(movieInfo.getMetaRating(), metaRating);
        Assert.assertEquals(movieInfo.getReleaseYear(), releaseYear);
    }
}