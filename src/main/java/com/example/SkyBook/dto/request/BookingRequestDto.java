package com.example.SkyBook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookingRequestDto(

        @NotNull(message = "Schedule ID is required")
        @Positive(message = "Schedule ID must be positive")
        Long scheduleId,

        @NotNull(message = "Seat ID is required")
        @Positive(message = "Seat ID must be positive")
        Long seatId,

        @NotNull(message = "Passenger details are required")
        @Valid
        PassengerRequestDto passenger


) {
}