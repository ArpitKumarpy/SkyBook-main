package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponseDto(

        Long id,
        String bookingReference,
        BookingStatus bookingStatus,
        BigDecimal totalAmount,
        LocalDateTime bookedAt,
        Long userId,
        Long scheduleId,
        Long seatId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}