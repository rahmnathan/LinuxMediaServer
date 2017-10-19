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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class MoviePushNotificationHandler implements DirectoryMonitorObserver {
    @Value("${push.notification.key}")
    private String pushKey;
    private PushNotificationHandler pushNotificationHandler;
    private Set<String> pushTokens = new HashSet<>();
    private final Logger logger = Logger.getLogger(MoviePushNotificationHandler.class.getName());

    @PostConstruct
    public void initialize(){
        pushNotificationHandler = new PushNotificationHandler(pushKey);
    }

    public void addPushToken(String token){
        if(!pushTokens.contains(token)){
            pushTokens.add(token);
        }
    }

    @Override
    public void directoryModified(WatchEvent watchEvent, Path path) {
        if(watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isRegularFile(path)){
            String fileName = path.getFileName().toString();
            logger.info("PushNotification handler detected new file - " + fileName);

            String mediaName = fileName.substring(0, fileName.lastIndexOf('.'));
            pushTokens.forEach(token -> {
                PushNotification pushNotification = PushNotification.Builder.newInstance()
                        .addData("Title", "New Movie!")
                        .addData("Body", mediaName)
                        .setRecipientToken(token)
                        .build();

                pushNotificationHandler.sendPushNotification(pushNotification);
            });
        }
    }
}
