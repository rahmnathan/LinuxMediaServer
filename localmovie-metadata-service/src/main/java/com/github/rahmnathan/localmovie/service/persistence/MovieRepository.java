package com.github.rahmnathan.localmovie.service.persistence;

import com.github.rahmnathan.localmovie.domain.MediaFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CrudRepository<MediaFile, String> {

}
