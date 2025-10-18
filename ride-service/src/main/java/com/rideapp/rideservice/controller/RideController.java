package com.rideapp.rideservice.controller;

import com.rideapp.rideservice.entity.Ride;
import com.rideapp.rideservice.service.RideService;
import com.rideapp.rideservice.security.JwtUtil;
import com.rideapp.rideservice.client.UserServiceClient;
import com.rideapp.rideservice.dto.UserInfoWithRatingDto;
import com.rideapp.rideservice.dto.RideDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    @Autowired
    private RideService rideService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceClient userServiceClient;

    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable("id") Long id) {
        Optional<Ride> ride = rideService.getRideById(id);
        return ride.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ride createRide(@RequestBody Ride ride, Authentication authentication, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long userId = null;
            try {
                userId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {
                // fallback: use authentication.getName() if userId is not present
            }
            if (userId != null) {
                ride.setUserId(userId);
            }
        }
        return rideService.createRide(ride);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{id}/accept")
    public ResponseEntity<Ride> acceptRide(@PathVariable("id") Long id, Authentication authentication, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long driverId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                driverId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {}
        }
        if (driverId == null) {
            return ResponseEntity.status(403).build();
        }
        Optional<Ride> rideOpt = rideService.getRideById(id);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Ride updatedRide = rideService.acceptRide(rideOpt.get(), driverId);
            return ResponseEntity.ok(updatedRide);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable("id") Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }

    // DRIVER: List available rides to accept
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/available")
    public List<Ride> getAvailableRides() {
        // Example: available rides have null driverId and status 'REQUESTED'
        return rideService.findAvailableRides();
    }

    // DRIVER: View assigned rides
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/assigned")
    public List<Ride> getAssignedRides(Authentication authentication, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long driverId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                driverId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {}
        }
        if (driverId == null) return List.of();
        return rideService.findRidesByDriverId(driverId);
    }

    // DRIVER: Complete a ride
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable("id") Long id, Authentication authentication, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long driverId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                driverId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {}
        }
        Optional<Ride> rideOpt = rideService.getRideById(id);
        if (rideOpt.isEmpty()) return ResponseEntity.notFound().build();
        try {
            Ride updatedRide = rideService.completeRide(rideOpt.get(), driverId);
            return ResponseEntity.ok(updatedRide);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // RIDER: View their own rides
    @PreAuthorize("hasRole('RIDER')")
    @GetMapping("/my")
    public List<Ride> getMyRides(Authentication authentication, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                userId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {}
        }
        if (userId == null) return List.of();
        return rideService.findRidesByUserId(userId);
    }

    // RIDER: Cancel a ride
    @PreAuthorize("hasRole('RIDER')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Ride> cancelRide(@PathVariable("id") Long id, Authentication authentication, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                userId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {}
        }
        Optional<Ride> rideOpt = rideService.getRideById(id);
        if (rideOpt.isEmpty()) return ResponseEntity.notFound().build();
        try {
            Ride updatedRide = rideService.cancelRide(rideOpt.get(), userId);
            return ResponseEntity.ok(updatedRide);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // RIDER: Rate a driver
    @PreAuthorize("hasRole('RIDER')")
    @PostMapping("/{id}/rate")
    public ResponseEntity<String> rateDriver(@PathVariable("id") Long id, @RequestParam int rating, Authentication authentication, HttpServletRequest request) {
        Optional<Ride> rideOpt = rideService.getRideById(id);
        if (rideOpt.isEmpty()) return ResponseEntity.notFound().build();
        String authHeader = request.getHeader("Authorization");
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                userId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {}
        }
        try {
            rideService.rateDriver(rideOpt.get(), rating, userId);
            return ResponseEntity.ok("Driver rated successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Cannot rate driver for this ride");
        }
    }

    // DRIVER: Rate a rider
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{id}/rate-rider")
    public ResponseEntity<String> rateRider(@PathVariable("id") Long id, @RequestParam int rating, Authentication authentication, HttpServletRequest request) {
        Optional<Ride> rideOpt = rideService.getRideById(id);
        if (rideOpt.isEmpty()) return ResponseEntity.notFound().build();
        String authHeader = request.getHeader("Authorization");
        Long driverId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                driverId = Long.valueOf(jwtUtil.getClaimFromToken(token, "userId"));
            } catch (Exception e) {}
        }
        try {
            rideService.rateRider(rideOpt.get(), rating, driverId);
            return ResponseEntity.ok("Rider rated successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Cannot rate rider for this ride");
        }
    }

    @GetMapping("/{id}/duration")
    public ResponseEntity<String> getRideDuration(@PathVariable("id") Long id) {
        Optional<Ride> rideOpt = rideService.getRideById(id);
        if (rideOpt.isEmpty()) return ResponseEntity.notFound().build();
        Ride ride = rideOpt.get();
        if (ride.getStartTime() != null && ride.getEndTime() != null) {
            Duration duration = Duration.between(ride.getStartTime(), ride.getEndTime());
            long minutes = duration.toMinutes();
            long seconds = duration.minusMinutes(minutes).getSeconds();
            return ResponseEntity.ok(minutes + " minutes, " + seconds + " seconds");
        } else {
            return ResponseEntity.ok("Ride is not completed yet or missing time info");
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getRideDetailsWithUserInfo(@PathVariable("id") Long id) {
        Optional<Ride> rideOpt = rideService.getRideById(id);
        if (rideOpt.isEmpty()) return ResponseEntity.notFound().build();
        Ride ride = rideOpt.get();
        UserInfoWithRatingDto driverInfo = null;
        UserInfoWithRatingDto riderInfo = null;
        if (ride.getDriverId() != null) {
            driverInfo = userServiceClient.getUserInfoWithRating(ride.getDriverId());
        }
        if (ride.getUserId() != null) {
            riderInfo = userServiceClient.getUserInfoWithRating(ride.getUserId());
        }
        return ResponseEntity.ok(new RideDetailsDto(ride, driverInfo, riderInfo));
    }
}
