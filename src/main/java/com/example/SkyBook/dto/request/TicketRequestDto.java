package com.example.SkyBook.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TicketRequestDto(

        @NotNull(message = "Booking ID is required")
        @Positive(message = "Booking ID must be positive")
        Long bookingId

) {
}