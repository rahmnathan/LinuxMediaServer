package com.github.rahmnathan.localmovies.pushnotification;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.google.pushnotification.control.PushNotificationHandler;
import com.github.rahmnathan.google.pushnotification.data.PushNotification;
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
    private PushNotificationHandler pushNotificationHandler;
    private final Logger logger = Logger.getLogger(MoviePushNotificationHandler.class.getName());

    @PostConstruct
    public void initialize(){
        pushNotificationHandler = new PushNotificationHandler(pushKey);
    }

    @Override
    public void directoryModified(WatchEvent watchEvent, Path path) {
        if(watchEvent == StandardWatchEventKinds.ENTRY_CREATE && Files.isRegularFile(path)){
            logger.info("PushNotification handler detected new file - " + path.getFileName());
            PushNotification pushNotification = PushNotification.Builder.newInstance()
                    .setNotificationTitle("New Movie")
                    .setRecipientToken(path.getFileName().toString())
                    .setRecipientToken("e2GZzaZjw1c:APA91bEjOc307sh1V9G2ZHRQeEjVzKoWRJWvE7UUqCaFqdd1gfp7HdhKFwJJTH1ctjlSpD" +
                            "TBpQTfEnhaZg06_yf43AilM5rbAzKVOqUS3lAjbjkn8cR6wHRYwKOICDn2M2Sj5kUYTgPK")
                    .build();

            pushNotificationHandler.sendPushNotification(pushNotification);
        }
    }
}
