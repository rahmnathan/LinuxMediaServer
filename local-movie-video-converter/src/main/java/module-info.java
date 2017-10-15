module com.github.rahmnathan.local.movie.video.converter {
    requires spring.context;
    requires com.github.rahmnathan.directory.monitor;
    requires java.logging;
    requires spring.beans;
    requires ffmpeg;
    requires java.xml.ws.annotation;
    requires com.github.rahmnathan.video.converter;
    exports com.github.rahmnathan.video.monitor;
}