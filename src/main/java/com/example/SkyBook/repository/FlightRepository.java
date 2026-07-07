package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findBySourceAndDestination(
            String source,
            String destination
    );


}