package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.FlightStatus;

import java.time.LocalDateTime;

public record FlightResponseDto(

        Long id,
        String flightNumber,
        String airlineName,
        String source,
        String destination,
        Double price,
        FlightStatus status,
        Long aircraftId,
        String aircraftModel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}