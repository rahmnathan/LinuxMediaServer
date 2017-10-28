package com.github.rahmnathan.localmovies.monitor;

import com.github.rahmnathan.localmovies.omdb.info.provider.OmdbMovieInfoProvider;
import com.github.rahmnathan.localmovies.persistence.MovieInfoRepository;
import com.github.rahmnathan.movie.info.api.IMovieInfoProvider;
import com.github.rahmnathan.movie.info.data.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

@Component
public class MovieInfoRepositoryMonitor {
    private final Logger logger = Logger.getLogger(MovieInfoRepositoryMonitor.class.getName());
    private final MovieInfoRepository movieInfoRepository;
    private IMovieInfoProvider movieInfoProvider;

    @Autowired
    public MovieInfoRepositoryMonitor(MovieInfoRepository movieInfoRepository, OmdbMovieInfoProvider movieInfoProvider) {
        this.movieInfoRepository = movieInfoRepository;
        this.movieInfoProvider = movieInfoProvider;
    }

    @Scheduled(fixedRate = 86400000)
    public void checkForEmptyValues(){
        logger.info("Checking for null MovieInfo fields in database");

        movieInfoRepository.findAll().forEach(movie -> {
            MovieInfo existingMovieInfo = movie.getMovieInfo();
            if(existingMovieInfo.hasMissingValues()){
                logger.info(existingMovieInfo.toString());

                String title;
                if(existingMovieInfo.getTitle().charAt(existingMovieInfo.getTitle().length() - 4) == '.'){
                    title = existingMovieInfo.getTitle().substring(0, existingMovieInfo.getTitle().length() - 4);
                } else {
                    title = existingMovieInfo.getTitle();
                }

                MovieInfo newMovieInfo = movieInfoProvider.loadMovieInfo(title);

                logger.info("Merging MovieInfo objects");
                MovieInfo mergedMovieInfo = MovieInfo.Builder.newInstance()
                        .setGenre(newMovieInfo.getGenre() != null && !newMovieInfo.getGenre().equals("") ? newMovieInfo.getGenre() : existingMovieInfo.getGenre())
                        .setImage(newMovieInfo.getImage() != null && !newMovieInfo.getImage().equals("") ? newMovieInfo.getImage() : existingMovieInfo.getImage())
                        .setIMDBRating(newMovieInfo.getIMDBRating() != null && !newMovieInfo.getIMDBRating().equals("") ? newMovieInfo.getIMDBRating() : existingMovieInfo.getIMDBRating())
                        .setMetaRating(newMovieInfo.getMetaRating() != null && !newMovieInfo.getMetaRating().equals("")? newMovieInfo.getMetaRating() : existingMovieInfo.getMetaRating())
                        .setReleaseYear(newMovieInfo.getReleaseYear() != null && !newMovieInfo.getReleaseYear().equals("") ? newMovieInfo.getReleaseYear() : existingMovieInfo.getReleaseYear())
                        .setTitle(newMovieInfo.getTitle() != null && !newMovieInfo.getTitle().equals("") ? newMovieInfo.getTitle() : existingMovieInfo.getTitle())
                        .build();

                movie.setMovieInfo(mergedMovieInfo);
            }
        });
    }
}
