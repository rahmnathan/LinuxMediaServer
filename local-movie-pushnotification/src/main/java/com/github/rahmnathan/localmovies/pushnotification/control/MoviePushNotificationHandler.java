package com.github.rahmnathan.localmovies.pushnotification.control;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.google.pushnotification.boundary.FirebaseNotificationService;
import com.github.rahmnathan.google.pushnotification.data.PushNotification;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushClient;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushTokenRepository;
import com.github.rahmnathan.localmovies.video.control.VideoConversionMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Optional;
import java.util.Set;

@ManagedBean
public class MoviePushNotificationHandler implements DirectoryMonitorObserver {
    private final Logger logger = LoggerFactory.getLogger(MoviePushNotificationHandler.class.getName());
    private final FirebaseNotificationService notificationService;
    private final AndroidPushTokenRepository pushTokenRepository;
    private final Set<String> activeConversions;

    public MoviePushNotificationHandler(AndroidPushTokenRepository pushTokenRepository, FirebaseNotificationService notificationService,
                                        VideoConversionMonitor videoConversionMonitor) {
        this.activeConversions = videoConversionMonitor.getActiveConversions();
        this.notificationService = notificationService;
        this.pushTokenRepository = pushTokenRepository;
    }

    public void addPushToken(AndroidPushClient pushClient) {
        Optional<AndroidPushClient> existingPushClient = pushTokenRepository.findById(pushClient.getDeviceId());

        if (existingPushClient.isPresent()) {
            AndroidPushClient managedPushClient = existingPushClient.get();
            if (!managedPushClient.getPushToken().equals(pushClient.getPushToken())) {
                managedPushClient.setPushToken(pushClient.getPushToken());
            }
        } else {
            pushTokenRepository.save(pushClient);
        }
    }

    @Override
    public void directoryModified(WatchEvent watchEvent, Path path) {
        if (isNewMovie(watchEvent, path)) {
            String fileName = path.getFileName().toString();
            logger.info("PushNotification handler detected new file - {}", fileName);

            String mediaName = fileName.substring(0, fileName.lastIndexOf('.'));
            pushTokenRepository.findAll().forEach(token -> {
                PushNotification pushNotification = PushNotification.Builder.newInstance()
                        .addData("title", "New Movie!")
                        .addData("body", mediaName)
                        .setRecipientToken(token.getPushToken())
                        .build();

                notificationService.sendPushNotification(pushNotification);
            });
        }
    }

    private boolean isNewMovie(WatchEvent watchEvent, Path path){
        return watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE
                && Files.isRegularFile(path)
                && !activeConversions.contains(path.toString());
    }
}
