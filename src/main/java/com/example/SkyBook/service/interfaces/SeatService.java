package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.SeatRequestDto;
import com.example.SkyBook.dto.response.SeatAvailabilityResponseDto;
import com.example.SkyBook.dto.response.SeatResponseDto;

import java.util.List;

public interface SeatService {

    SeatResponseDto createSeat(SeatRequestDto request);

    List<SeatResponseDto> getAllSeats();

    SeatResponseDto getSeatById(Long id);

    SeatResponseDto updateSeat(Long id, SeatRequestDto request);

    void deleteSeat(Long id);

    List<SeatResponseDto> getSeatsByAircraft(Long aircraftId);

    List<SeatAvailabilityResponseDto> getSeatAvailability(Long scheduleId);
}