package com.github.rahmnathan.localmovie.service.control;

import com.github.rahmnathan.localmovie.domain.MediaFile;
import com.github.rahmnathan.localmovie.domain.MovieClient;
import com.github.rahmnathan.localmovie.domain.MovieSearchCriteria;
import com.github.rahmnathan.omdb.data.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.rahmnathan.localmovie.domain.MovieOrder.RATING;

public class MediaFileUtilsTest {
    private List<MediaFile> mediaFileList;

    @BeforeEach
    public void initialize(){
        this.mediaFileList = new ArrayList<>();

        Movie movie = Movie.Builder.newInstance()
                .setActors("test actor")
                .setGenre("action")
                .setImage("")
                .setIMDBRating("4")
                .setTitle("myTitle")
                .setReleaseYear("2001")
                .build();

        MediaFile mediaFile = MediaFile.Builder.newInstance()
                .setFileName("Test.mkv")
                .setPath("Movies/Test.mkv")
                .setViews(1)
                .setMovie(movie)
                .build();

        mediaFileList.add(mediaFile);

        movie = Movie.Builder.newInstance()
                .setActors("test actor")
                .setGenre("action")
                .setImage("")
                .setIMDBRating("5")
                .setTitle("myTitle")
                .setReleaseYear("2005")
                .build();

        mediaFile = MediaFile.Builder.newInstance()
                .setFileName("Test1.mkv")
                .setPath("Movies/Test.mkv")
                .setViews(3)
                .setMovie(movie)
                .build();

        mediaFileList.add(mediaFile);
    }

    @Test
    public void sortMediaFilesTest(){
        MovieSearchCriteria searchCriteria = new MovieSearchCriteria("Series", 0, 10, MovieClient.ANDROID, RATING);

        List<MediaFile> resultList = MediaFileUtils.sortMediaFiles(searchCriteria, mediaFileList);

        Assertions.assertEquals("Test1.mkv", resultList.get(0).getFileName());
    }

    @Test
    public void paginateMediaFilesTest(){
        MovieSearchCriteria searchCriteria = new MovieSearchCriteria("Series", 0, 1, MovieClient.ANDROID, RATING);

        List<MediaFile> resultList = MediaFileUtils.paginateMediaFiles(mediaFileList, searchCriteria);

        Assertions.assertEquals(1, resultList.size());
    }

    @Test
    public void removePosterImagesTest(){
        List<MediaFile> resultList = MediaFileUtils.removePosterImages(mediaFileList);

        Assertions.assertEquals("noImage", resultList.get(0).getMovie().getImage());
    }
}
