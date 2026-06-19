package com.keltron.dogbreeder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.keltron.dogbreeder",
        "com.keltron.utility"
})
@EntityScan(basePackages = {
        "com.keltron.utility.jpa.entity",
        "com.keltron.dogbreeder.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.keltron.dogbreeder.repository",
        "com.keltron.utility.jpa.repository"
})
public class DogbreederServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                DogbreederServiceApplication.class,
                args);
    }
}