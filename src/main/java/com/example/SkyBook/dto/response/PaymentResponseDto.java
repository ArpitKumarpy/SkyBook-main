package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.PaymentMethod;
import com.example.SkyBook.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDto(

        Long id,
        String paymentReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String transactionId,
        LocalDateTime paymentTime,
        Long bookingId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}