package com.example.SkyBook.dto.request;

import com.example.SkyBook.enums.FlightStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FlightRequestDto(

        @NotBlank(message = "Flight number is required")
        String flightNumber,

        @NotBlank(message = "Airline name is required")
        String airlineName,

        @NotBlank(message = "Source is required")
        String source,

        @NotBlank(message = "Destination is required")
        String destination,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        Double price,

        @NotNull(message = "Flight status is required")
        FlightStatus status,

        @NotNull(message = "Aircraft ID is required")
        Long aircraftId

) {
}