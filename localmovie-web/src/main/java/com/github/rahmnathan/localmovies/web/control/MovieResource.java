package com.github.rahmnathan.localmovies.web.control;

import com.github.rahmnathan.localmovie.domain.MediaFile;
import com.github.rahmnathan.localmovie.domain.MediaFileEvent;
import com.github.rahmnathan.localmovie.domain.MovieInfoRequest;
import com.github.rahmnathan.localmovie.domain.MovieSearchCriteria;
import com.github.rahmnathan.localmovie.service.boundary.MediaMetadataService;
import com.github.rahmnathan.localmovies.event.control.MediaFileEventManager;
import com.github.rahmnathan.localmovies.event.control.PushNotificationHandler;
import com.github.rahmnathan.localmovies.event.data.AndroidPushClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
public class MovieResource {
    private final Logger logger = LoggerFactory.getLogger(MovieResource.class.getName());
    private final PushNotificationHandler notificationHandler;
    private static final String TRANSACTION_ID = "TransactionID";
    private final MediaMetadataService mediaMetadataService;
    private final FileSender fileSender = new FileSender();
    private final MediaFileEventManager eventManager;
    private final String[] mediaPaths;

    private static final Counter MOVIES_COUNTER = Metrics.counter("localmovies.movies.request.counter");
    private static final Counter COUNT_COUNTER = Metrics.counter("localmovies.count.request.counter");
    private static final Counter POSTER_COUNTER = Metrics.counter("localmovies.poster.request.counter");
    private static final Counter STREAM_COUNTER = Metrics.counter("localmovies.stream.request.counter");
    private static final Counter EVENTS_COUNTER = Metrics.counter("localmovies.events.request.counter");


    public MovieResource(MediaMetadataService mediaMetadataService, PushNotificationHandler notificationHandler,
                         @Value("${media.path}") String[] mediaPaths,
                         MediaFileEventManager eventManager){
        this.notificationHandler = notificationHandler;
        this.mediaMetadataService = mediaMetadataService;
        this.eventManager = eventManager;
        this.mediaPaths = mediaPaths;
    }

    @PostMapping(value = "/localmovies/v2/movies", produces=MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MediaFile> getMovies(@RequestBody MovieInfoRequest movieInfoRequest, HttpServletResponse response) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        MOVIES_COUNTER.increment();
        logger.info("Received request: {}", movieInfoRequest.toString());

        if(movieInfoRequest.getPushToken() != null && movieInfoRequest.getDeviceId() != null){
            AndroidPushClient pushClient = new AndroidPushClient(movieInfoRequest.getDeviceId(), movieInfoRequest.getPushToken());
            notificationHandler.addPushToken(pushClient);
        }

        MovieSearchCriteria searchCriteria = new MovieSearchCriteria(movieInfoRequest.getPath(), movieInfoRequest.getPage(),
                movieInfoRequest.getResultsPerPage(), movieInfoRequest.getClient(), movieInfoRequest.getOrder());

        Integer page = searchCriteria.getPage();
        if(page != null && page == 0)
            getMovieCount(movieInfoRequest.getPath(), response);

        List<MediaFile> movieInfoList = mediaMetadataService.loadMediaFileList(searchCriteria);

        logger.info("Returning {} movies", movieInfoList.size());
        MDC.clear();
        return movieInfoList;
    }

    @GetMapping(value = "/localmovies/v2/movies/count")
    public void getMovieCount(@RequestParam(value = "path") String path, HttpServletResponse response){
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        COUNT_COUNTER.increment();
        logger.info("Received count request for path - {}", path);

        int count = mediaMetadataService.loadMediaListLength(path);
        logger.info("Returning count of - {}", count);
        response.setHeader("Count", String.valueOf(count));
        MDC.clear();
    }

    /**
     * @param path - Path to video file to stream
     */
    @GetMapping(value = "/localmovies/v2/movie/stream.mp4", produces = "video/mp4")
    public void streamVideo(@RequestParam("path") String path, HttpServletResponse response, HttpServletRequest request) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        STREAM_COUNTER.increment();
        response.setHeader("Access-Control-Allow-Origin", "*");

        MediaFile movie = mediaMetadataService.loadSingleMediaFile(path);
        movie.addView();
        logger.info("Received streaming request - {}", path);
        for(String mediaPath : mediaPaths) {
            if (new File(mediaPath + path).exists()) {
                logger.info("Streaming - {}{}", mediaPath, path);
                response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
                fileSender.serveResource(Paths.get(mediaPath + path), request, response);
                break;
            }
        }
        MDC.clear();
    }

    /**
     * @param path - Path to video file
     * @return - Poster image for specified video file
     */
    @GetMapping(path = "/localmovies/v2/movie/poster")
    public ResponseEntity<byte[]> getPoster(@RequestParam("path") String path) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        POSTER_COUNTER.increment();
        logger.info("Streaming poster - {}", path);

        String image = mediaMetadataService.loadSingleMediaFile(path).getMovie().getImage();
        if(image == null)
            return ResponseEntity.ok(new byte[0]);

        byte[] poster = Base64.getDecoder().decode(image);
        MDC.clear();
        return ResponseEntity.ok(poster);
    }

    @GetMapping(path = "/localmovies/v2/movie/events")
    public ResponseEntity<List<MediaFileEvent>> getPoster(@RequestParam("timestamp") Long epoch) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        EVENTS_COUNTER.increment();

        logger.info("Request for events since: {}", epoch);
        List<MediaFileEvent> events = eventManager.getMediaFileEvents(LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()));

        MDC.clear();
        return ResponseEntity.ok(events);
    }
}