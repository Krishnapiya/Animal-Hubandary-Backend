package com.example.springSecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.keltron.utility.jpa.entity.RoleMaster;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;
import com.example.springSecurity.repository.AppUserRepository;
import com.example.springSecurity.repository.RoleMasterRepository;

@SpringBootApplication
@EntityScan(basePackageClasses = { Users.class, RoleMaster.class })
@EnableJpaRepositories(basePackageClasses = {
        AppUserRepository.class,
        UsersRepository.class,
        RoleMasterRepository.class
})
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}

}
