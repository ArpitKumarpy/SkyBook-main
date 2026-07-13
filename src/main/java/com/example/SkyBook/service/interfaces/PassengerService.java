package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.PassengerRequestDto;
import com.example.SkyBook.dto.response.PassengerResponseDto;

import java.util.List;

public interface PassengerService {

    PassengerResponseDto createPassenger(PassengerRequestDto request);
    PassengerResponseDto getPassengerById(Long id);
    List<PassengerResponseDto> getAllPassengers();
    List<PassengerResponseDto> getPassengersByBooking(Long bookingId);
    PassengerResponseDto updatePassenger(Long id, PassengerRequestDto request);
    void deletePassenger(Long id);

}