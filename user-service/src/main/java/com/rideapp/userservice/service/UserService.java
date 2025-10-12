package com.rideapp.userservice.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rideapp.userservice.dto.UserDTO;
import com.rideapp.userservice.dto.UserRequest;
import com.rideapp.userservice.entity.User;
import com.rideapp.userservice.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO register(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            // Handle email already exists case
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Validate role
        String roleStr = request.getRole();
        try {
            user.setRole(User.Role.valueOf(roleStr.toUpperCase()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid role: " + roleStr);
        }

        User saved = userRepository.save(user);

        return toDTO(saved);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::toDTO).toList();
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return toDTO(user);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        // Optionally add id if UserDTO supports it
        // dto.setId(user.getId());
        return dto;
    }


}
