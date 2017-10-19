package com.github.rahmnathan.localmovies.pushnotification.control;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.google.pushnotification.control.PushNotificationHandler;
import com.github.rahmnathan.google.pushnotification.data.PushNotification;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushClient;
import com.github.rahmnathan.localmovies.pushnotification.persistence.AndroidPushTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.logging.Logger;

@Component
public class MoviePushNotificationHandler implements DirectoryMonitorObserver {
    @Value("${push.notification.key}")
    private String pushKey;
    private final AndroidPushTokenRepository pushTokenRepository;
    private PushNotificationHandler pushNotificationHandler;
    private final Logger logger = Logger.getLogger(MoviePushNotificationHandler.class.getName());

    @Autowired
    public MoviePushNotificationHandler(AndroidPushTokenRepository pushTokenRepository){
        this.pushTokenRepository = pushTokenRepository;
    }

    @PostConstruct
    public void initialize(){
        pushNotificationHandler = new PushNotificationHandler(pushKey);
    }

    public void addPushToken(AndroidPushClient pushClient){
        if(pushTokenRepository.exists(pushClient.getDeviceId())){
            AndroidPushClient managedPushClient = pushTokenRepository.findOne(pushClient.getDeviceId());
            if(!managedPushClient.getPushToken().equals(pushClient.getPushToken())){
                managedPushClient.setPushToken(pushClient.getPushToken());
            }
        } else {
            pushTokenRepository.save(pushClient);
        }
    }

    @Override
    public void directoryModified(WatchEvent watchEvent, Path path) {
        if(watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isRegularFile(path)){
            String fileName = path.getFileName().toString();
            logger.info("PushNotification handler detected new file - " + fileName);

            String mediaName = fileName.substring(0, fileName.lastIndexOf('.'));
            pushTokenRepository.findAll().forEach(token -> {
                PushNotification pushNotification = PushNotification.Builder.newInstance()
                        .addData("Title", "New Movie!")
                        .addData("Body", mediaName)
                        .setRecipientToken(token.getPushToken())
                        .build();

                pushNotificationHandler.sendPushNotification(pushNotification);
            });
        }
    }
}
