package com.github.rahmnathan.localmovies.service.persistence;

import com.github.rahmnathan.localmovies.service.persistence.data.MediaFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CrudRepository<MediaFile, String> {

}
