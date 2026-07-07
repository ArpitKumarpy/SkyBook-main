package com.example.SkyBook.dto.request;

import com.example.SkyBook.enums.SeatClass;
import com.example.SkyBook.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatRequestDto(

        @NotBlank(message = "Seat number is required")
        String seatNumber,

        @NotNull(message = "Seat class is required")
        SeatClass seatClass,

        @NotNull(message = "Seat type is required")
        SeatType seatType,

        @NotNull(message = "Aircraft ID is required")
        Long aircraftId

) {
}