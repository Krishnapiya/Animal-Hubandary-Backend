package com.example.springSecurity.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.springSecurity.dto.ChangePassword;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final UsersRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public String changePassword(ChangePassword request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("username " + username);
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
        
    
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            System.out.println("Mismatch: " + request.getCurrentPassword() + " vs " + user.getPassword());
            return "Current password is incorrect";
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return "New password and confirmation do not match";
        }

        user.setPassword(user.hashPassword(request.getNewPassword())); 
        userRepository.save(user);

        System.out.println("Password updated for user: " + username);
        return "Password updated successfully";
    }
}

