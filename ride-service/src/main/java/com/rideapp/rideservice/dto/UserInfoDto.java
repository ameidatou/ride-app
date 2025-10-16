package com.rideapp.rideservice.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Double averageRating;
}
