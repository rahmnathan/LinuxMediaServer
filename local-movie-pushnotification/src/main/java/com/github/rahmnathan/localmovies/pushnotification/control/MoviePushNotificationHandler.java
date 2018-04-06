package com.github.rahmnathan.localmovies.pushnotification.control;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.google.pushnotification.boundary.FirebaseNotificationService;
import com.github.rahmnathan.google.pushnotification.data.PushNotification;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushClient;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

@ManagedBean
public class MoviePushNotificationHandler implements DirectoryMonitorObserver {
    private final Logger logger = LoggerFactory.getLogger(MoviePushNotificationHandler.class.getName());
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

    @Override
    public void directoryModified(WatchEvent watchEvent, Path path) {
        if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isRegularFile(path)) {
            String fileName = path.getFileName().toString();
            logger.info("PushNotification handler detected new file - {}", fileName);

            String mediaName = fileName.substring(0, fileName.lastIndexOf('.'));
            pushTokenRepository.findAll().forEach(token -> {
                PushNotification pushNotification = PushNotification.Builder.newInstance()
                        .addData("Title", "New Movie!")
                        .addData("Body", mediaName)
                        .setRecipientToken(token.getPushToken())
                        .build();

                notificationService.sendPushNotification(pushNotification);
            });
        }
    }
}
