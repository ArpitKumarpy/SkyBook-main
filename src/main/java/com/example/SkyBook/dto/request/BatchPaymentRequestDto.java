package com.example.SkyBook.dto.request;

import com.example.SkyBook.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record BatchPaymentRequestDto(

        @NotEmpty(message = "At least one booking ID is required")
        List<@NotNull @Positive Long> bookingIds,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod

) {
}