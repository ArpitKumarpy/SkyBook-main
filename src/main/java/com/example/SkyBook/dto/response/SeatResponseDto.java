package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.SeatClass;
import com.example.SkyBook.enums.SeatType;

import java.time.LocalDateTime;

public record SeatResponseDto(

        Long id,
        String seatNumber,
        SeatClass seatClass,
        SeatType seatType,
        Long aircraftId,
        String aircraftModel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}