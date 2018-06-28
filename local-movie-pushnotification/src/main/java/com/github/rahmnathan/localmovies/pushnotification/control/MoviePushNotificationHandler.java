package com.github.rahmnathan.localmovies.pushnotification.control;

import com.github.rahmnathan.google.pushnotification.boundary.FirebaseNotificationService;
import com.github.rahmnathan.google.pushnotification.data.PushNotification;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushClient;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;

@ManagedBean
public class MoviePushNotificationHandler {
    private final FirebaseNotificationService notificationService;
    private final AndroidPushTokenRepository pushTokenRepository;

    public MoviePushNotificationHandler(AndroidPushTokenRepository pushTokenRepository, FirebaseNotificationService notificationService) {
        this.notificationService = notificationService;
        this.pushTokenRepository = pushTokenRepository;
    }

    public void addPushToken(AndroidPushClient pushClient) {
        if (pushTokenRepository.exists(pushClient.getDeviceId())) {
            AndroidPushClient managedPushClient = pushTokenRepository.findOne(pushClient.getDeviceId());
            if (!managedPushClient.getPushToken().equals(pushClient.getPushToken())) {
                managedPushClient.setPushToken(pushClient.getPushToken());
            }
        } else {
            pushTokenRepository.save(pushClient);
        }
    }

    public void sendPushNotifications(String fileName) {
        String mediaName = fileName.substring(0, fileName.lastIndexOf('.'));
        pushTokenRepository.findAll().forEach(token -> {
            PushNotification pushNotification = PushNotification.Builder.newInstance()
                    .setRecipientToken(token.getPushToken())
                    .addData("title", "New Movie!")
                    .addData("body", mediaName)
                    .build();

            notificationService.sendPushNotification(pushNotification);
        });
    }
}
