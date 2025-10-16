package com.rideapp.rideservice.service;

import com.rideapp.rideservice.entity.Ride;
import com.rideapp.rideservice.entity.Ride.Status;
import com.rideapp.rideservice.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {
    @Autowired
    private RideRepository rideRepository;

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Optional<Ride> getRideById(Long id) {
        return rideRepository.findById(id);
    }

    public Ride createRide(Ride ride) {
        return rideRepository.save(ride);
    }

    public void deleteRide(Long id) {
        rideRepository.deleteById(id);
    }

    public List<Ride> findAvailableRides() {
        return rideRepository.findByDriverIdIsNullAndStatus(Status.REQUESTED.name());
    }

    public Ride acceptRide(Ride ride, Long driverId) {
        if (ride.getStatus() == Status.REQUESTED && ride.getDriverId() == null) {
            ride.setDriverId(driverId);
            ride.setStatus(Status.ACCEPTED);
            ride.setStartTime(LocalDateTime.now());
            return rideRepository.save(ride);
        }
        throw new IllegalStateException("Ride cannot be accepted");
    }

    public Ride completeRide(Ride ride, Long driverId) {
        if (ride.getStatus() == Status.ACCEPTED && driverId.equals(ride.getDriverId())) {
            ride.setStatus(Status.COMPLETED);
            ride.setEndTime(LocalDateTime.now());
            return rideRepository.save(ride);
        }
        throw new IllegalStateException("Ride cannot be completed");
    }

    public Ride cancelRide(Ride ride, Long userId) {
        if ((ride.getStatus() == Status.REQUESTED || ride.getStatus() == Status.ACCEPTED) && userId.equals(ride.getUserId())) {
            ride.setStatus(Status.CANCELLED);
            return rideRepository.save(ride);
        }
        throw new IllegalStateException("Ride cannot be cancelled");
    }

    public Ride rateDriver(Ride ride, Integer rating, Long userId) {
        if (ride.getStatus() == Status.COMPLETED && userId.equals(ride.getUserId())) {
            ride.setRating(rating);
            return rideRepository.save(ride);
        }
        throw new IllegalStateException("Cannot rate driver for this ride");
    }

    public Ride rateRider(Ride ride, Integer rating, Long driverId) {
        if (ride.getStatus() == Status.COMPLETED && driverId.equals(ride.getDriverId())) {
            ride.setRiderRating(rating);
            return rideRepository.save(ride);
        }
        throw new IllegalStateException("Cannot rate rider for this ride");
    }

    public List<Ride> findRidesByDriverId(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public List<Ride> findRidesByUserId(Long userId) {
        return rideRepository.findByUserId(userId);
    }
}
