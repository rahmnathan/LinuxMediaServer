package com.github.rahmnathan.localmovies.pushnotification.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AndroidPushTokenRepository extends CrudRepository<AndroidPushClient, String> {

}
