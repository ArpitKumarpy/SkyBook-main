package com.example.SkyBook.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ScheduleResponseDto(


        Long id,
        LocalDate departureDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Integer availableSeats,

        Long flightId,
        String flightNumber,
        String airlineName,
        String source,
        String destination,
        Double price,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}