package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.ScheduleRequestDto;
import com.example.SkyBook.dto.response.ScheduleResponseDto;
import com.example.SkyBook.entity.Schedule;

public class ScheduleMapper {

    private ScheduleMapper() {
    }

    public static Schedule fromRequestDto(ScheduleRequestDto dto) {

        return Schedule.builder()
                .departureDate(dto.departureDate())
                .departureTime(dto.departureTime())
                .arrivalTime(dto.arrivalTime())
                .build();
    }

    public static ScheduleResponseDto toResponseDto(Schedule schedule) {

        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getDepartureDate(),
                schedule.getDepartureTime(),
                schedule.getArrivalDate(),
                schedule.getArrivalTime(),
                schedule.getAvailableSeats(),
                schedule.getFlight().getId(),
                schedule.getFlight().getFlightNumber(),
                schedule.getFlight().getAirlineName(),
                schedule.getFlight().getSource(),
                schedule.getFlight().getDestination(),
                schedule.getFlight().getPrice(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
