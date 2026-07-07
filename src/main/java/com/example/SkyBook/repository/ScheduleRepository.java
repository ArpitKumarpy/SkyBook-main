package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByDepartureDate(LocalDate departureDate);
    List<Schedule> findByFlight_Id(Long flightId);
    @Query("""
       SELECT s
       FROM Schedule s
       WHERE LOWER(s.flight.source) = LOWER(:source)
       AND LOWER(s.flight.destination) = LOWER(:destination)
       AND s.departureDate = :departureDate
       """)
    List<Schedule> searchFlights(
            @RequestParam("source") String source,
            @RequestParam("destination") String destination,
            @RequestParam("departureDate") LocalDate departureDate
    );

}