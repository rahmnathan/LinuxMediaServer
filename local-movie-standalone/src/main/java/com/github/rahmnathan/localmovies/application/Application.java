package com.github.rahmnathan.localmovies.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.github.rahmnathan")
@EntityScan(basePackages = "com.github.rahmnathan")
@EnableCaching
@ComponentScan(basePackages = "com.github.rahmnathan")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}