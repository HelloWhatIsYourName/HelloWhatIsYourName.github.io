package com.dataglove.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DataGloveWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataGloveWebApplication.class, args);
    }
}