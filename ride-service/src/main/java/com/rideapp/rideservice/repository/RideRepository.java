package com.rideapp.rideservice.repository;

import com.rideapp.rideservice.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    // Custom query methods can be added here
}
