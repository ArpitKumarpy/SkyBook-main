package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.AircraftRequestDto;
import com.example.SkyBook.dto.response.AircraftResponseDto;
import com.example.SkyBook.entity.Aircraft;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.AircraftMapper;
import com.example.SkyBook.repository.AircraftRepository;
import com.example.SkyBook.service.interfaces.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;

    @Override
    public AircraftResponseDto createAircraft(AircraftRequestDto request) {

        if (aircraftRepository.existsByModelNo(request.getModelNo())) {
            throw new DuplicateResourceException("Aircraft model already exists.");
        }

        Aircraft aircraft = AircraftMapper.toEntity(request);

        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        return AircraftMapper.toResponse(savedAircraft);
    }

    @Override
    public List<AircraftResponseDto> getAllAircraft() {

        return aircraftRepository.findAll()
                .stream()
                .map(AircraftMapper::toResponse)
                .toList();
    }

    @Override
    public AircraftResponseDto getAircraftById(Long id) {

        Aircraft aircraft = aircraftRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Aircraft not found with id : " + id));
        return AircraftMapper.toResponse(aircraft);
    }

    @Override
    public AircraftResponseDto updateAircraft(Long id, AircraftRequestDto request) {

       Aircraft aircraft = aircraftRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Aircraft not found with id : " + id));
        aircraft.setModelNo(request.getModelNo());
        aircraft.setTotalSeats(request.getTotalSeats());

        Aircraft updatedAircraft = aircraftRepository.save(aircraft);

        return AircraftMapper.toResponse(updatedAircraft);
    }

    @Override
    public void deleteAircraft(Long id) {

       Aircraft aircraft = aircraftRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Aircraft not found with id : " + id));

        aircraftRepository.delete(aircraft);
    }
}