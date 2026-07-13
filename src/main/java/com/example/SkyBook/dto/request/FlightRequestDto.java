package com.example.SkyBook.dto.request;

import com.example.SkyBook.enums.FlightStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FlightRequestDto(

        @NotBlank(message = "Flight number is required")
        @Size(max = 20)
        String flightNumber,

        @NotBlank(message = "Airline name is required")
        @Size(max = 100)
        String airlineName,

        @NotBlank(message = "Source is required")
        @Size(max = 100)
        String source,

        @NotBlank(message = "Destination is required")
        @Size(max = 100)
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