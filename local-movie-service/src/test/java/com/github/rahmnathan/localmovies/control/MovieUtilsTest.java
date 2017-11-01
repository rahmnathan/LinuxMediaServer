package com.github.rahmnathan.localmovies.control;

import org.junit.Assert;
import org.junit.Test;

public class MovieUtilsTest {

    @Test
    public void testTopLevel(){
        String path = "Movies/test-movie.mp4";

        Assert.assertTrue(MovieUtils.isTopLevel(path));

        path = "Series/Game of Thrones/Season 1";

        Assert.assertFalse(MovieUtils.isTopLevel(path));
    }

    @Test
    public void testGetTitle(){
        String title = "test-movie.mp4";

        Assert.assertEquals("test-movie", MovieUtils.getTitle(title));

        title = "Game of Thrones";

        Assert.assertEquals("Game of Thrones", MovieUtils.getTitle(title));
    }

    @Test
    public void testGetParentFile(){
        String path = "Series/Game of Thrones/Season 1/test-episode.mp4";

        Assert.assertEquals("Game of Thrones", MovieUtils.getParentFile(path).getName());

        path = "Series/Game of Thrones/Season 1";

        Assert.assertEquals("Game of Thrones", MovieUtils.getParentFile(path).getName());

    }
}
