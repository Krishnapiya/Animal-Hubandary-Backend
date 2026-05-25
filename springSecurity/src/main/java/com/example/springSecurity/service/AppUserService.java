package com.example.springSecurity.service;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import com.example.springSecurity.entity.AppUser;
import com.example.springSecurity.repository.AppUserRepository;
import com.keltron.utility.jpa.entity.Users;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<String> roleNames = appUserRepository.findRoleNamesByUsername(username);
        if (roleNames.isEmpty()) {
            roleNames = List.of("ADMIN");
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())  // must be BCrypt hash
                .roles(roleNames.stream().map(String::toUpperCase).toArray(String[]::new))
                .build();
    }
}
