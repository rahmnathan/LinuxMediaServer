package com.github.rahmnathan.localmovies.event.control;

import com.github.rahmnathan.google.pushnotification.boundary.FirebaseNotificationService;
import com.github.rahmnathan.google.pushnotification.data.PushNotification;
import com.github.rahmnathan.localmovies.event.data.AndroidPushClient;
import com.github.rahmnathan.localmovies.event.repository.AndroidPushTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.util.Optional;

@ManagedBean
public class MoviePushNotificationHandler {
    private final Logger logger = LoggerFactory.getLogger(MoviePushNotificationHandler.class);
    private final FirebaseNotificationService notificationService;
    private final AndroidPushTokenRepository pushTokenRepository;

    public MoviePushNotificationHandler(AndroidPushTokenRepository pushTokenRepository, FirebaseNotificationService notificationService) {
        this.notificationService = notificationService;
        this.pushTokenRepository = pushTokenRepository;
    }

    public void addPushToken(AndroidPushClient pushClient) {
        Optional<AndroidPushClient> optionalPushClient = pushTokenRepository.findById(pushClient.getDeviceId());
        if (optionalPushClient.isPresent()) {
            AndroidPushClient managedPushClient = optionalPushClient.get();
            if (!managedPushClient.getPushToken().equals(pushClient.getPushToken())) {
                managedPushClient.setPushToken(pushClient.getPushToken());
            }
        } else {
            pushTokenRepository.save(pushClient);
        }
    }

    public void sendPushNotifications(String fileName, String path) {
        logger.info("Sending notification of new movie: {} to {} clients", path, pushTokenRepository.count());
        pushTokenRepository.findAll().forEach(token -> {
            PushNotification pushNotification = PushNotification.Builder.newInstance()
                    .setRecipientToken(token.getPushToken())
                    .setTitle("New Movie!")
                    .setBody(fileName)
                    .addData("path", path)
                    .build();

            notificationService.sendPushNotification(pushNotification);
        });
    }
}
