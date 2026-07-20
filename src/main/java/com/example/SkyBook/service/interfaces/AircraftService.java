package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.AircraftRequestDto;
import com.example.SkyBook.dto.response.AircraftResponseDto;

import java.util.List;

public interface AircraftService {

    AircraftResponseDto createAircraft(AircraftRequestDto request);
    List<AircraftResponseDto> getAllAircraft();
    AircraftResponseDto getAircraftById(Long id);
    AircraftResponseDto updateAircraft(Long id, AircraftRequestDto request);
    void deleteAircraft(Long id);

}