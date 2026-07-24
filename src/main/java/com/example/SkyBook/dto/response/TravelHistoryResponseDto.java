package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.TravelStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelHistoryResponseDto {

    private String bookingReference;
    private String flightNumber;
    private String airlineName;
    private String source;
    private String destination;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private TravelStatus travelStatus;
}