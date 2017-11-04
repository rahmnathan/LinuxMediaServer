package com.github.rahmnathan.localmovies.control;

import com.github.rahmnathan.localmovies.utils.PathUtils;
import org.junit.Assert;
import org.junit.Test;

public class MediaFileUtilsTest {

    @Test
    public void testTopLevel(){
        String path = "Movies/test-movie.mp4";

        Assert.assertTrue(PathUtils.isTopLevel(path));

        path = "Series/Game of Thrones/Season 1";

        Assert.assertFalse(PathUtils.isTopLevel(path));
    }

    @Test
    public void testGetTitle(){
        String title = "test-movie.mp4";

        Assert.assertEquals("test-movie", PathUtils.getTitle(title));

        title = "Game of Thrones";

        Assert.assertEquals("Game of Thrones", PathUtils.getTitle(title));
    }

    @Test
    public void testGetParentFile(){
        String path = "Series/Game of Thrones/Season 1/test-episode.mp4";

        Assert.assertEquals("Game of Thrones", PathUtils.getParentFile(path).getName());

        path = "Series/Game of Thrones/Season 1";

        Assert.assertEquals("Game of Thrones", PathUtils.getParentFile(path).getName());

    }
}
