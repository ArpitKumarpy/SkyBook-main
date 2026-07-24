package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.SeatAvailabilityStatus;
import com.example.SkyBook.enums.SeatClass;
import com.example.SkyBook.enums.SeatType;

public record SeatAvailabilityResponseDto(

        Long seatId,

        String seatNumber,

        SeatClass seatClass,

        SeatType seatType,

        SeatAvailabilityStatus status

) {
}