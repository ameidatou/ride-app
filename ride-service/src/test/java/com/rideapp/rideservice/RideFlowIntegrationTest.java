package com.rideapp.rideservice;

import com.rideapp.rideservice.entity.Ride;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class RideFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // Helper to build URL
    private String url(String path) {
        return "http://localhost:" + port + "/api/rides" + path;
    }

    private String generateTestJwt(String userId, String role, String subject) {
        String secret = "your-ride-service-jwt-secret-key";
        long expiration = 3600000L;
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .claim("sub", subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    @Test
    void testFullRideFlow() {
        // 1. Rider creates a ride
        String riderJwt = generateTestJwt("1", "RIDER", "rider1");
        String driverJwt = generateTestJwt("2", "DRIVER", "driver2");
        Ride rideRequest = new Ride();
        rideRequest.setOrigin("A");
        rideRequest.setDestination("B");
        rideRequest.setStatus(Ride.Status.REQUESTED);
        // Set other required fields as needed
        HttpHeaders riderHeaders = new HttpHeaders();
        riderHeaders.set("Authorization", "Bearer " + riderJwt);
        HttpEntity<Ride> createReq = new HttpEntity<>(rideRequest, riderHeaders);
        ResponseEntity<Ride> createResp = restTemplate.postForEntity(url(""), createReq, Ride.class);
        Assertions.assertEquals(HttpStatus.OK, createResp.getStatusCode());
        Ride createdRide = createResp.getBody();
        Assertions.assertNotNull(createdRide);
        Long rideId = createdRide.getId();
        Assertions.assertEquals(Ride.Status.REQUESTED, createdRide.getStatus());

        // 2. Driver accepts the ride
        HttpHeaders driverHeaders = new HttpHeaders();
        driverHeaders.set("Authorization", "Bearer " + driverJwt);
        HttpEntity<Void> acceptReq = new HttpEntity<>(driverHeaders);
        ResponseEntity<Ride> acceptResp = restTemplate.exchange(url("/" + rideId + "/accept"), HttpMethod.POST, acceptReq, Ride.class);
        Assertions.assertEquals(HttpStatus.OK, acceptResp.getStatusCode());
        Ride acceptedRide = acceptResp.getBody();
        Assertions.assertNotNull(acceptedRide);
        Assertions.assertEquals(Ride.Status.ACCEPTED, acceptedRide.getStatus());

        // 3. Driver completes the ride
        HttpEntity<Void> completeReq = new HttpEntity<>(driverHeaders);
        ResponseEntity<Ride> completeResp = restTemplate.exchange(url("/" + rideId + "/complete"), HttpMethod.POST, completeReq, Ride.class);
        Assertions.assertEquals(HttpStatus.OK, completeResp.getStatusCode());
        Ride completedRide = completeResp.getBody();
        Assertions.assertNotNull(completedRide);
        Assertions.assertEquals(Ride.Status.COMPLETED, completedRide.getStatus());

        // 4. Rider rates the driver
        HttpEntity<Void> rateReq = new HttpEntity<>(riderHeaders);
        ResponseEntity<String> rateResp = restTemplate.exchange(url("/" + rideId + "/rate?rating=5"), HttpMethod.POST, rateReq, String.class);
        Assertions.assertEquals(HttpStatus.OK, rateResp.getStatusCode());
        String rateRespBody = rateResp.getBody();
        Assertions.assertNotNull(rateRespBody);
        Assertions.assertTrue(rateRespBody.contains("Driver rated successfully"));

        // 5. Driver rates the rider
        ResponseEntity<String> rateRiderResp = restTemplate.exchange(url("/" + rideId + "/rate-rider?rating=5"), HttpMethod.POST, completeReq, String.class);
        Assertions.assertEquals(HttpStatus.OK, rateRiderResp.getStatusCode());
        String rateRiderRespBody = rateRiderResp.getBody();
        Assertions.assertNotNull(rateRiderRespBody);
        Assertions.assertTrue(rateRiderRespBody.contains("Rider rated successfully"));
    }
}
