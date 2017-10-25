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

    @Value("${omdb.api.key}")
    private String apiKey;
    private final Logger logger = Logger.getLogger(MovieInfoRepositoryMonitor.class.getName());
    private final MovieInfoRepository movieInfoRepository;
    private IMovieInfoProvider movieInfoProvider;

    @Autowired
    public MovieInfoRepositoryMonitor(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    @PostConstruct
    public void initialize(){
        this.movieInfoProvider = new OmdbMovieInfoProvider(apiKey);
    }

    @Scheduled(fixedRate = 86400000)
    public void checkForEmptyValues(){
        logger.info("Checking for null MovieInfo fields in database");

        movieInfoRepository.findAll().forEach(movie -> {
            MovieInfo existingMovieInfo = movie.getMovieInfo();
            if(existingMovieInfo.hasMissingValues()){
                logger.info(existingMovieInfo.getTitle() + " contains null fields");

                String title;
                if(existingMovieInfo.getTitle().charAt(existingMovieInfo.getTitle().length() - 4) == '.'){
                    title = existingMovieInfo.getTitle().substring(existingMovieInfo.getTitle().length() - 4);
                } else {
                    title = existingMovieInfo.getTitle();
                }

                MovieInfo newMovieInfo = movieInfoProvider.loadMovieInfo(title);

                MovieInfo mergedMovieInfo = MovieInfo.Builder.newInstance()
                        .setGenre(newMovieInfo.getGenre() != null ? newMovieInfo.getGenre() : existingMovieInfo.getGenre())
                        .setImage(newMovieInfo.getImage() != null ? newMovieInfo.getImage() : existingMovieInfo.getImage())
                        .setIMDBRating(newMovieInfo.getIMDBRating() != null ? newMovieInfo.getIMDBRating() : existingMovieInfo.getIMDBRating())
                        .setMetaRating(newMovieInfo.getMetaRating() != null ? newMovieInfo.getMetaRating() : existingMovieInfo.getMetaRating())
                        .setReleaseYear(newMovieInfo.getReleaseYear() != null ? newMovieInfo.getReleaseYear() : existingMovieInfo.getReleaseYear())
                        .setTitle(newMovieInfo.getTitle() != null ? newMovieInfo.getTitle() : existingMovieInfo.getTitle())
                        .build();

                movie.setMovieInfo(mergedMovieInfo);
            }
        });
    }
}
