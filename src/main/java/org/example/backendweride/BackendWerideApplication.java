package org.example.backendweride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication

@EnableJpaRepositories(basePackages = "org.example.backendweride.platform")

@EntityScan(basePackages = "org.example.backendweride.platform")
public class BackendWerideApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendWerideApplication.class, args);
    }

}