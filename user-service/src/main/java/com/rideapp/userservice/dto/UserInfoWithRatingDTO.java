package com.rideapp.userservice.dto;

import lombok.Data;

@Data
public class UserInfoWithRatingDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Double averageRating;
}
