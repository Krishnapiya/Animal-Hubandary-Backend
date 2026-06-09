package com.keltron.petshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = { "com.keltron.petshop", "com.keltron.utility" })
@EntityScan(basePackages = {
	    "com.keltron.utility.jpa.entity",  // Ensure this is the correct package
		"com.keltron.petshop.entity"
	})
public class PetshopServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetshopServiceApplication.class, args);
	}

}