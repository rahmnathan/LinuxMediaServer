package com.github.rahmnathan.localmovie.service.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathUtilsTest {

    @Test
    public void getFileNameTest(){
        String fileName = "test.mkv";
        String dirName = "test";

        Assertions.assertEquals(dirName, PathUtils.getTitle(fileName));
        Assertions.assertEquals(dirName, PathUtils.getTitle(dirName));
    }

    @Test
    public void isTopLevelTest(){
        String topLevelDir = "Series/Game of Thrones";
        String nonTopLevelDir = "Series/Game of Thrones/Season 1";

        Assertions.assertTrue(PathUtils.isTopLevel(topLevelDir));
        Assertions.assertFalse(PathUtils.isTopLevel(nonTopLevelDir));
    }

    @Test
    public void getParentFileTest(){
        String originalDir = "Series/Game of Thrones/Season 1";
        String originalFile = "Series/Game of Thrones/Season 1/Episode 1.mkv";

        Assertions.assertEquals("Series/Game of Thrones", PathUtils.getParentFile(originalDir).toString());
        Assertions.assertEquals("Series/Game of Thrones", PathUtils.getParentFile(originalFile).toString());
    }
}
