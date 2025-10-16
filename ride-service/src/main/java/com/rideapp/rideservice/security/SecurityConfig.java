package com.rideapp.rideservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // Allow POST /api/rides for RIDER (create ride)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/rides").hasRole("RIDER")
                // Allow POST /api/rides/{id}/accept, /complete, /rate-rider for DRIVER
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/rides/*/accept", "/api/rides/*/complete", "/api/rides/*/rate-rider").hasRole("DRIVER")
                // Allow POST /api/rides/{id}/rate for RIDER
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/rides/*/rate").hasRole("RIDER")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/rides/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
