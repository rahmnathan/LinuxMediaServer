package com.github.rahmnathan.localmovies.persistence;

import com.github.rahmnathan.localmovies.data.MediaFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieInfoRepository extends CrudRepository<MediaFile, String> {

}
