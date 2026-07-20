package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.SeatRequestDto;
import com.example.SkyBook.dto.response.SeatResponseDto;
import com.example.SkyBook.entity.Seat;

public class SeatMapper {

    private SeatMapper() {
    }

    public static Seat fromRequestDto(SeatRequestDto dto) {

        return Seat.builder()
                .seatNumber(dto.seatNumber())
                .seatClass(dto.seatClass())
                .seatType(dto.seatType())
                .build();
    }

    public static SeatResponseDto toResponseDto(Seat seat) {

        return new SeatResponseDto(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getSeatClass(),
                seat.getSeatType(),
                seat.getAircraft().getId(),
                seat.getAircraft().getModelNo(),
                seat.getCreatedAt(),
                seat.getUpdatedAt()
        );
    }
}