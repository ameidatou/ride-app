package com.rideapp.rideservice.repository;

import com.rideapp.rideservice.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByUserId(Long userId);
    List<Ride> findByDriverIdIsNullAndStatus(String status);
}
