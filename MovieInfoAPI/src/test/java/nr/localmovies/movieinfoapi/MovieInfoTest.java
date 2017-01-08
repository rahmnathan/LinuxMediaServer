package nr.localmovies.movieinfoapi;

import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class MovieInfoTest {

    @Test
    public void movieInfoEntityTest(){
        EntityManager entityManager = Persistence.createEntityManagerFactory("localmoviestest").createEntityManager();

        MovieInfoEntity originalEntity = new MovieInfoEntity("TestId", "TestData");
        entityManager.persist(originalEntity);

        MovieInfoEntity entityFromDatabase = entityManager.find(MovieInfoEntity.class, "TestId");

        Assert.assertEquals(entityFromDatabase.getData(), originalEntity.getData());
        Assert.assertEquals(entityFromDatabase.getId(), originalEntity.getId());
    }

    @Test
    public void movieInfoTest(){
        String title = "TestTitle";
        String image = "TestBase64Image";
        String IMDBRating = "TestIMDBRating";
        String metaRating = "TestMetaRating";
        String releaseYear = "TestReleaseYear";

        MovieInfo movieInfo = new MovieInfo();
        movieInfo.setTitle(title)
                .setImage(image)
                .setIMDBRating(IMDBRating)
                .setMetaRating(metaRating)
                .setReleaseYear(releaseYear);

        Assert.assertEquals(movieInfo.getTitle(), title);
        Assert.assertEquals(movieInfo.getImage(), image);
        Assert.assertEquals(movieInfo.getIMDBRating(), IMDBRating);
        Assert.assertEquals(movieInfo.getMetaRating(), metaRating);
        Assert.assertEquals(movieInfo.getReleaseYear(), releaseYear);
    }
}