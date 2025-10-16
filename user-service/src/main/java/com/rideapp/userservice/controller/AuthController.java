package com.rideapp.userservice.controller;

import com.rideapp.userservice.entity.User;
import com.rideapp.userservice.repository.UserRepository;
import com.rideapp.userservice.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = JwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
