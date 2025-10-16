package com.rideapp.rideservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;
    private String destination;
    private LocalDateTime pickupTime;
    private Long userId;
    private Long driverId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public enum Status {
        REQUESTED, ACCEPTED, COMPLETED, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer rating; // rating given by rider to driver
    private Integer riderRating; // rating given by driver to rider
}
