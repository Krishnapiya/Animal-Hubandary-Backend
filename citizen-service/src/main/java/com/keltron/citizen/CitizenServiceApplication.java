package com.keltron.citizen;

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
@EnableJpaRepositories(basePackages = {
        "com.keltron.citizen.repository"
})
@ComponentScan(basePackages = {
        "com.keltron.citizen",
        "com.keltron.utility"
})
@EntityScan(basePackages = {
        "com.keltron.citizen.entity",
        "com.keltron.utility.jpa.entity"
})
public class CitizenServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitizenServiceApplication.class, args);
    }

}