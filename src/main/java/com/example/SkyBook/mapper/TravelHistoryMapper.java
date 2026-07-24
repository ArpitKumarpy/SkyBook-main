package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.response.TravelHistoryResponseDto;
import com.example.SkyBook.entity.TravelHistory;
import org.springframework.stereotype.Component;

@Component
public class TravelHistoryMapper {

    public TravelHistoryResponseDto toResponseDto(TravelHistory travelHistory) {

        return TravelHistoryResponseDto.builder()
                .bookingReference(travelHistory.getBooking().getBookingReference())
                .flightNumber(travelHistory.getSchedule().getFlight().getFlightNumber())
                .airlineName(travelHistory.getSchedule().getFlight().getAirlineName())
                .source(travelHistory.getSchedule().getFlight().getSource())
                .destination(travelHistory.getSchedule().getFlight().getDestination())
                .departureDate(travelHistory.getSchedule().getDepartureDate())
                .departureTime(travelHistory.getSchedule().getDepartureTime())
                .arrivalTime(travelHistory.getSchedule().getArrivalTime())
                .travelStatus(travelHistory.getTravelStatus())
                .build();
    }
}