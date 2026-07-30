package com.example.springSecurity.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springSecurity.dto.PetShopOwnerRegisterDto;
import com.example.springSecurity.repository.AppUserRepository;
import com.keltron.utility.jpa.entity.RoleMaster;
import com.keltron.utility.jpa.entity.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetShopOwnerRegistrationService {

    private static final String PET_SHOP_OWNER_ROLE = "PET_SHOP_OWNER";
    private static final String DOG_BREEDER_ROLE = "DOG_BREEDER";
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[0-9]{10}$");

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, String> validate(PetShopOwnerRegisterDto dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            errors.put("detail", "Request body is required");
            return errors;
        }
        if (isBlank(dto.getFname())) {
            errors.put("fname", "First name is required");
        }
        if (isBlank(dto.getLname())) {
            errors.put("lname", "Last name is required");
        }
        if (isBlank(dto.getEmail()) || !EMAIL_PATTERN.matcher(dto.getEmail().trim()).matches()) {
            errors.put("email", "Valid email is required");
        } else if (appUserRepository.existsByEmailIgnoreCase(dto.getEmail().trim())) {
            errors.put("email", "Email is already registered");
        }
        if (isBlank(dto.getMobileNo()) || !MOBILE_PATTERN.matcher(dto.getMobileNo().trim()).matches()) {
            errors.put("mobileNo", "Valid 10-digit mobile number is required");
        }
        if (isBlank(dto.getUsername()) || dto.getUsername().trim().length() < 4) {
            errors.put("username", "Username must be at least 4 characters");
        } else if (appUserRepository.findByUsername(dto.getUsername().trim()).isPresent()) {
            errors.put("username", "Username is already taken");
        }
        if (isBlank(dto.getPassword()) || dto.getPassword().length() < 8) {
            errors.put("password", "Password must be at least 8 characters");
        }
        if (dto.getPassword() != null
                && !dto.getPassword().equals(dto.getConfirmPassword())) {
            errors.put("confirmPassword", "Passwords do not match");
        }
        String roleName =
                "DOG_BREEDER".equalsIgnoreCase(dto.getOwnerType())
                        ? DOG_BREEDER_ROLE
                        : PET_SHOP_OWNER_ROLE;

        if (!errors.containsKey("username")
                && appUserRepository.findRoleIdByRoleName(roleName).isEmpty()) {

            errors.put("detail", roleName + " role is not configured.");
        }

        return errors;
    }

    @Transactional
    public Users register(PetShopOwnerRegisterDto dto) {

        String roleName;

        if ("DOG_BREEDER".equalsIgnoreCase(dto.getOwnerType())) {
            roleName = DOG_BREEDER_ROLE;
        } else {
            roleName = PET_SHOP_OWNER_ROLE;
        }

        Integer roleId = appUserRepository
                .findRoleIdByRoleName(roleName)
                .orElseThrow(() ->
                        new IllegalStateException("Role not found : " + roleName));

        Users user = new Users();

        user.setFname(dto.getFname().trim());
        user.setLname(dto.getLname().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setMobileNo(dto.getMobileNo().trim());
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        RoleMaster role = new RoleMaster(roleId);
        role.setRoleName(roleName);

        user.setRole(role);

        return appUserRepository.save(user);
    }
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
