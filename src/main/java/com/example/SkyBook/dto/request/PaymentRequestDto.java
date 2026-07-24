package com.example.SkyBook.dto.request;

import com.example.SkyBook.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequestDto(

        @NotNull(message = "Booking ID is required")
        @Positive(message = "Booking ID must be positive")
        Long bookingId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod

) {
}