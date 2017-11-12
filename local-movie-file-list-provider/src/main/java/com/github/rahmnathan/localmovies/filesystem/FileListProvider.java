package com.github.rahmnathan.localmovies.filesystem;

import com.github.rahmnathan.directory.monitor.DirectoryMonitorObserver;
import com.github.rahmnathan.keycloak.auth.utils.KeycloakUtils;
import org.apache.camel.ProducerTemplate;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class FileListProvider implements FileRepository, DirectoryMonitorObserver {

    @Value("${media.path}")
    private String[] mediaPaths;
    @Value("${localmovies.keycloak.username}")
    private String username;
    @Value("${localmovies.keycloak.password}")
    private String password;
    private final Logger logger = Logger.getLogger(FileListProvider.class.getName());
    private final ProducerTemplate producerTemplate;

    @Autowired
    public FileListProvider(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @CacheEvict(value = "files", allEntries = true)
    public void directoryModified(WatchEvent event, Path absolutePath) {
        logger.info("Detected " + event.kind().name() + " " + absolutePath.toString());
        logger.info("Purging cache");

        String accessToken = KeycloakUtils.getAccessToken(username, password, producerTemplate);
        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        producerTemplate.sendBodyAndHeaders("seda:filecachepurge", absolutePath.toFile().getName(), headers);
    }

    @Cacheable(value = "files")
    public Set<String> listFiles(String path) {
        logger.info("Listing files at - " + path);

        Set<String> filePaths = new HashSet<>();
        Arrays.stream(mediaPaths).forEach(mediaPath -> {
                    Optional<File[]> fileArray = Optional.ofNullable(new File(mediaPath + path).listFiles());

                    fileArray.ifPresent(files -> {
                        Set<String> tempFileSet = Arrays.stream(files)
                                .map(file -> file.getAbsolutePath().substring(mediaPath.length()))
                                .collect(Collectors.toSet());

                        filePaths.addAll(tempFileSet);
                    });
                });

        return filePaths;
    }
}
