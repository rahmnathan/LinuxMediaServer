package com.github.rahmnathan.localmovies.event.repository;

import com.github.rahmnathan.localmovie.domain.MediaFileEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaEventRepository extends CrudRepository<MediaFileEvent, Long> {
}
