package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.SeatRequestDto;
import com.example.SkyBook.dto.response.SeatResponseDto;
import com.example.SkyBook.entity.Aircraft;
import com.example.SkyBook.entity.Seat;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.SeatMapper;
import com.example.SkyBook.repository.AircraftRepository;
import com.example.SkyBook.repository.SeatRepository;
import com.example.SkyBook.service.interfaces.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final AircraftRepository aircraftRepository;

    @Override
    public SeatResponseDto createSeat(SeatRequestDto request) {

        if (seatRepository.existsBySeatNumberAndAircraftId(
                request.seatNumber(),
                request.aircraftId())) {

            throw new DuplicateResourceException("Seat already exists for this aircraft.");
        }

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aircraft not found with id : " + request.aircraftId()));

        Seat seat = SeatMapper.fromRequestDto(request);

        seat.setAircraft(aircraft);

        Seat savedSeat = seatRepository.save(seat);

        return SeatMapper.toResponseDto(savedSeat);
    }

    @Override
    public List<SeatResponseDto> getAllSeats() {

        return seatRepository.findAll()
                .stream()
                .map(SeatMapper::toResponseDto)
                .toList();
    }

    @Override
    public SeatResponseDto getSeatById(Long id) {

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found with id : " + id));

        return SeatMapper.toResponseDto(seat);
    }

    @Override
    public SeatResponseDto updateSeat(Long id, SeatRequestDto request) {

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found with id : " + id));

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aircraft not found with id : " + request.aircraftId()));

        seat.setSeatNumber(request.seatNumber());
        seat.setSeatClass(request.seatClass());
        seat.setSeatType(request.seatType());
        seat.setAircraft(aircraft);

        Seat updatedSeat = seatRepository.save(seat);

        return SeatMapper.toResponseDto(updatedSeat);
    }

    @Override
    public void deleteSeat(Long id) {

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found with id : " + id));

        seatRepository.delete(seat);
    }

    @Override
    public List<SeatResponseDto> getSeatsByAircraft(Long aircraftId) {

        return seatRepository.findByAircraft_Id(aircraftId)
                .stream()
                .map(SeatMapper::toResponseDto)
                .toList();
    }
}