package com.github.rahmnathan.localmovies.filesystem.config;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.HttpMethod;
import java.util.logging.Logger;

@Component
public class FileCachePurgeConfig {
    private final Logger logger = Logger.getLogger(FileCachePurgeConfig.class.getName());
    private final CamelContext camelContext;

    @Autowired
    public FileCachePurgeConfig(CamelContext camelContext){
        this.camelContext = camelContext;
    }

    @PostConstruct
    public void initialize(){
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    onException(Exception.class)
                            .useExponentialBackOff()
                            .redeliveryDelay(1000)
                            .maximumRedeliveries(5);

                    from("seda:filecachepurge")
                            .setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.PUT))
                            .to("https4://localmovies-cloud.hopto.org/movie-api/filelist/modified");
                }
            });
        } catch (Exception e){
            logger.severe(e.toString());
        }
    }
}
