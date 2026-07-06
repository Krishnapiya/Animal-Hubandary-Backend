package com.keltron.petshop.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collection;
import java.util.List;




@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true)
public class SecurityConfig {
	
	@Value("${security.jwt.secret-key}")
    private String jwtSecretKey;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	    return http.csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                    .requestMatchers("/").permitAll()
	                    .requestMatchers("/petshop/auth/application-document/view/**").permitAll()
	                    .anyRequest().authenticated()
	            )
	            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .build();
	}
	
	@Bean
    public JwtDecoder jwtDecoder() {
        var secretKey = new SecretKeySpec(jwtSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256).build();
    }

	
	@Bean	
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
		return jwtAuthenticationConverter;
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Object rolesClaim = jwt.getClaims().get("roles");
		if (rolesClaim instanceof Collection<?> roles && !roles.isEmpty()) {
			return roles.stream()
					.map(String::valueOf)
					.map(String::toUpperCase)
					.<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role))
					.toList();
		}
		String role = jwt.getClaimAsString("role");
		if (role != null && !role.isBlank()) {
			return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
		}
		return List.of();
	}
}
