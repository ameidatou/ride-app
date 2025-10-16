package com.rideapp.rideservice.dto;

import com.rideapp.rideservice.entity.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideDetailsDto {
    private Ride ride;
    private UserInfoWithRatingDto driver;
    private UserInfoWithRatingDto rider;
}
