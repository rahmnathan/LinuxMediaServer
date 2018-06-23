package com.github.rahmnathan.localmovies.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaEventRepository extends CrudRepository<MediaFileEvent, Long> {
}
