package com.example.springSecurity.controller;



import com.example.springSecurity.dto.ChangePassword;
import com.example.springSecurity.dto.LoginDto;
import com.example.springSecurity.dto.RegisterDto;
import com.example.springSecurity.entity.AppUser;
import com.example.springSecurity.repository.AppUserRepository;
import com.example.springSecurity.service.ChangePasswordService;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.requests.Request;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AppUserController {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    @Autowired
    AppUserRepository appUserRepository;
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    private final ChangePasswordService changePasswordService;
 
//    @PostMapping("/register")
//	public ResponseEntity<Object> register(
//			 @RequestBody RegisterDto registerDto){
//		var bCryptEncoder = new BCryptPasswordEncoder();
//		AppUser appUser = new AppUser();
//		appUser.setFirstName(registerDto.getFirstName());
//		appUser.setLastName(registerDto.getLastName());
//		appUser.setUsername(registerDto.getUsername());
//		appUser.setEmail(registerDto.getEmail());
//		appUser.setRole(registerDto.getRole());
//		appUser.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
//		appUserRepository.save(appUser);
//		
//		String jwtToken = createJwtToken(appUser);
//		var response = new HashMap<String, Object>();
//		response.put("token", jwtToken);
//		response.put("user", appUser);
//		return ResponseEntity.ok(response);
//	}
    
    
//    
    @PostMapping("/login")
	public ResponseEntity<Object> login(
			@RequestBody LoginDto loginDto){
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
				(loginDto.getUsername(), loginDto.getPassword())
				);
		Optional<Users> appUser = appUserRepository.findByUsername(loginDto.getUsername());
		String jwtToken = createJwtToken(appUser.get());
		var response = new HashMap<String, Object>();

		response.put("token", jwtToken);
		response.put("user", appUser);
		
		return ResponseEntity.ok(response);
		
}
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        try {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(
                    new SecretKeySpec(jwtSecretKey.getBytes(), "HmacSHA256")).build();

            Jwt jwt = decoder.decode(token);
            boolean isValid = jwt.getExpiresAt().isAfter(Instant.now());

            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }
    private String createJwtToken(Users appUser) {
        Instant now = Instant.now();
        List<String> roleNames = appUserRepository.findRoleNamesByUsername(appUser.getUsername());
        if (roleNames.isEmpty()) {
            roleNames = List.of("ADMIN");
        }
        List<String> normalizedRoles = roleNames.stream().map(String::toUpperCase).toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(24 * 3600))
                .subject(appUser.getUsername())
                .claim("role", normalizedRoles.get(0))
                .claim("roles", normalizedRoles)
                .build();
        var encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(jwtSecretKey.getBytes()));
        var params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),claims);
        return encoder.encode(params).getTokenValue();
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody Request<ChangePassword> request) {
        if (!request.isValid()) {
            return ResponseEntity.badRequest().body("Invalid request payload");
        }
        String response = changePasswordService.changePassword(request.getPayLoad());
        if (response.startsWith("Password updated")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

}