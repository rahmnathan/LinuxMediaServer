package com.github.rahmnathan.localmovies.event.repository;

import com.github.rahmnathan.localmovies.event.data.AndroidPushClient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AndroidPushTokenRepository extends CrudRepository<AndroidPushClient, String> {

}
