package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.FlightRequestDto;
import com.example.SkyBook.dto.response.FlightResponseDto;
import com.example.SkyBook.dto.response.FlightSearchResponseDto;
import com.example.SkyBook.enums.FlightStatus;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

public interface FlightService {

    FlightResponseDto createFlight(FlightRequestDto request);

    Page<FlightResponseDto> getAllFlights(
            int page,
            int size,
            String sortBy,
            String direction);

    FlightResponseDto getFlightById(Long id);

    FlightResponseDto updateFlight(Long id, FlightRequestDto request);

    void deleteFlight(Long id);

    List<FlightResponseDto> searchFlights(String source, String destination);

    List<FlightSearchResponseDto> searchFlights(

            String source,
            String destination,
            LocalDate departureDate,

            String airline,
            Double minPrice,
            Double maxPrice,

            FlightStatus status

    );
}