package com.example.SkyBook.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRequestDto(

        @NotNull(message = "Departure date is required")
        @FutureOrPresent(message = "Departure date cannot be in the past")
        LocalDate departureDate,

        @NotNull(message = "Departure time is required")
        LocalTime departureTime,

        @NotNull(message = "Arrival time is required")
        LocalTime arrivalTime,

        @NotNull(message = "Flight ID is required")
        @Positive(message = "Flight ID must be positive")
        Long flightId

) {
}