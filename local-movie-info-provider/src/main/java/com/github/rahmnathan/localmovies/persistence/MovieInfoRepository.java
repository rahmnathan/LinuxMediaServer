package com.github.rahmnathan.localmovies.persistence;

import com.github.rahmnathan.localmovies.data.MovieInfoWithContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieInfoRepository extends CrudRepository<MovieInfoWithContext, String> {

}
