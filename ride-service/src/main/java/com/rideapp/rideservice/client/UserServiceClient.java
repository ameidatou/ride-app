package com.rideapp.rideservice.client;

import com.rideapp.rideservice.dto.UserInfoDto;
import com.rideapp.rideservice.dto.UserInfoWithRatingDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String userServiceUrl = "http://user-service:8080/api/users/";

    public UserInfoDto getUserInfo(Long userId) {
        return restTemplate.getForObject(userServiceUrl + userId, UserInfoDto.class);
    }

    public UserInfoWithRatingDto getUserInfoWithRating(Long userId) {
        return restTemplate.getForObject(userServiceUrl + userId + "/info", UserInfoWithRatingDto.class);
    }
}
