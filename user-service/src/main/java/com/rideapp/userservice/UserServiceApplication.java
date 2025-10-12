package com.rideapp.userservice;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.rideapp.userservice.entity.User;
import com.rideapp.userservice.entity.User.Role;
import com.rideapp.userservice.repository.UserRepository;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			userRepository.deleteAll();
			userRepository.saveAll(
				List.of(
					new User(null, "John Doe", "john.doe@example.com", "password", Role.RIDER, false),
					new User(null, "Jane Doe", "jane.doe@example.com", "password", Role.DRIVER, false)
				)
			);
		};
	}

}
