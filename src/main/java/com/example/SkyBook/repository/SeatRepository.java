package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    boolean existsBySeatNumberAndAircraftId(
        String seatNumber,
        Long aircraftId
    );

    List<Seat> findByAircraft_Id(Long aircraftId);

}