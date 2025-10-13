package com.rideapp.rideservice.service;

import com.rideapp.rideservice.entity.Ride;
import com.rideapp.rideservice.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
