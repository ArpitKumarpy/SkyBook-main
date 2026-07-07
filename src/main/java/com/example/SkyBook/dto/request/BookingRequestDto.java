package com.example.SkyBook.dto.request;

public record BookingRequestDto(

        Long userId,
        Long scheduleId,
        Long seatId

) {
}