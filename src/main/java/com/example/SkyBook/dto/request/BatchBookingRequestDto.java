package com.example.SkyBook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BatchBookingRequestDto(

        @NotNull(message = "Schedule ID is required")
        Long scheduleId,

        @NotEmpty(message = "At least one seat must be selected")
        @Valid
        List<SeatBookingDto> seats

) {
    public record SeatBookingDto(

            @NotNull(message = "Seat ID is required")
            Long seatId,

            @NotNull(message = "Passenger details are required")
            @Valid
            PassengerRequestDto passenger

    ) {
    }
}