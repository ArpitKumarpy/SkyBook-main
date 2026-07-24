package com.example.SkyBook.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.SkyBook.enums.FlightStatus;

public record FlightSearchResponseDto(

        Long flightId,
        String flightNumber,
        String airlineName,
        String source,
        String destination,
        LocalDate departureDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Double price,
        Integer availableSeats,
        FlightStatus status

) {
}