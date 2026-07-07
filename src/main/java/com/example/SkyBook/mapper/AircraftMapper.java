package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.AircraftRequestDto;
import com.example.SkyBook.dto.response.AircraftResponseDto;
import com.example.SkyBook.entity.Aircraft;

public class AircraftMapper {

    public static Aircraft toEntity(AircraftRequestDto dto) {

        return Aircraft.builder()
                .modelNo(dto.getModelNo())
                .totalSeats(dto.getTotalSeats())
                .build();
    }

    public static AircraftResponseDto toResponse(Aircraft aircraft) {

        return AircraftResponseDto.builder()
                .id(aircraft.getId())
                .modelNo(aircraft.getModelNo())
                .totalSeats(aircraft.getTotalSeats())
                .build();
    }
}