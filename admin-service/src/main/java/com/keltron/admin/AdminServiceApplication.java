package com.keltron.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.keltron.admin")
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = { "com.keltron.admin", "com.keltron.utility" })
@EntityScan(basePackages = {
	    "com.keltron.utility.jpa.entity",
	    "com.keltron.admin.rbac.entity",
	    "com.keltron.admin.entity"
	})
public class AdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServiceApplication.class, args);
	}

}
