package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.FlightRequestDto;
import com.example.SkyBook.dto.response.FlightResponseDto;
import com.example.SkyBook.entity.Flight;

public class FlightMapper {

    private FlightMapper() {
    }

    public static Flight toEntity(FlightRequestDto dto) {

        return Flight.builder()
                .flightNumber(dto.flightNumber())
                .airlineName(dto.airlineName())
                .source(dto.source())
                .destination(dto.destination())
                .price(dto.price())
                .status(dto.status())
                .build();
    }

    public static FlightResponseDto toResponse(Flight flight) {

        return new FlightResponseDto(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAirlineName(),
                flight.getSource(),
                flight.getDestination(),
                flight.getPrice(),
                flight.getStatus(),
                flight.getAircraft().getId(),
                flight.getAircraft().getModelNo(),
                flight.getCreatedAt(),
                flight.getUpdatedAt()
        );
    }
}