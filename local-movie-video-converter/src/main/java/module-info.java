module local.movie.video.converter {
    requires spring.context;
    requires directory.monitor;
    requires spring.beans;
    requires ffmpeg;
    requires video.converter;
    requires javaee.api;
    requires slf4j.api;
}