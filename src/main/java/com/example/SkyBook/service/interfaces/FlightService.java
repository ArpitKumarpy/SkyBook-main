package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.FlightRequestDto;
import com.example.SkyBook.dto.response.FlightResponseDto;
import com.example.SkyBook.dto.response.FlightSearchResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {

    FlightResponseDto createFlight(FlightRequestDto request);
    List<FlightResponseDto> getAllFlights();
    FlightResponseDto getFlightById(Long id);
    FlightResponseDto updateFlight(Long id, FlightRequestDto request);
    void deleteFlight(Long id);
    List<FlightResponseDto> searchFlights(String source,String destination);
    List<FlightSearchResponseDto> searchFlights(String source, String destination,LocalDate departureDate);
    
}