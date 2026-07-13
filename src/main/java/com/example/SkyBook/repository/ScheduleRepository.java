package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.enums.FlightStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

                  AND (:airline IS NULL
                        OR LOWER(s.flight.airlineName) = LOWER(:airline))

                  AND (:status IS NULL
                        OR s.flight.status = :status)

                  AND (:minPrice IS NULL
                        OR s.flight.price >= :minPrice)

                  AND (:maxPrice IS NULL
                        OR s.flight.price <= :maxPrice)
            """)
    List<Schedule> searchFlights(

            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate,

            @Param("airline") String airline,
            @Param("status") FlightStatus status,

            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

}