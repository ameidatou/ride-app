package com.rideapp.rideservice.dto;

import lombok.Data;

@Data
public class UserInfoWithRatingDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Double averageRating;
}
