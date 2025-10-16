package com.rideapp.userservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideapp.userservice.dto.UserDTO;
import com.rideapp.userservice.dto.UserRequest;
import com.rideapp.userservice.dto.UserInfoWithRatingDTO;
import com.rideapp.userservice.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRequest request) {
        UserDTO userDTO = userService.register(request);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        UserDTO userDTO = userService.getUserByEmail(email);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers();
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<UserInfoWithRatingDTO> getUserInfoWithRating(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserInfoWithRating(id));
    }
}
