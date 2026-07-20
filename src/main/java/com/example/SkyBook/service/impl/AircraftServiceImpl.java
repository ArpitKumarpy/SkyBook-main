package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.AircraftRequestDto;
import com.example.SkyBook.dto.response.AircraftResponseDto;
import com.example.SkyBook.entity.Aircraft;
import com.example.SkyBook.entity.Seat;
import com.example.SkyBook.enums.SeatClass;
import com.example.SkyBook.enums.SeatType;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.AircraftMapper;
import com.example.SkyBook.repository.AircraftRepository;
import com.example.SkyBook.repository.SeatRepository;
import com.example.SkyBook.service.interfaces.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private static final char[] SEAT_LETTERS = {'A', 'B', 'C', 'D', 'E', 'F'};

    private final AircraftRepository aircraftRepository;
    private final SeatRepository seatRepository;

    @Override
    public AircraftResponseDto createAircraft(AircraftRequestDto request) {

        if (aircraftRepository.existsByModelNo(request.getModelNo())) {
            throw new DuplicateResourceException("Aircraft model already exists.");
        }

        Aircraft aircraft = AircraftMapper.toEntity(request);
        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        seatRepository.saveAll(buildSeatLayout(savedAircraft));

        return AircraftMapper.toResponse(savedAircraft);
    }

    @Override
    public List<AircraftResponseDto> getAllAircraft() {
        return aircraftRepository.findAll().stream().map(AircraftMapper::toResponse).toList();
    }

    @Override
    public AircraftResponseDto getAircraftById(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id : " + id));
        return AircraftMapper.toResponse(aircraft);
    }

    @Override
    public AircraftResponseDto updateAircraft(Long id, AircraftRequestDto request) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id : " + id));

        boolean seatCountChanged = !request.getTotalSeats().equals(aircraft.getTotalSeats());

        aircraft.setModelNo(request.getModelNo());
        aircraft.setTotalSeats(request.getTotalSeats());
        Aircraft updatedAircraft = aircraftRepository.save(aircraft);

        if (seatCountChanged) {
            // The old seat rows are stale the moment totalSeats changes — wipe
            // and regenerate a fresh, correctly-numbered layout rather than
            // leaving the previous seats sitting alongside (or short of) the
            // new count. If any of those old seats already have bookings
            // against them, this delete fails with a FK violation (surfaced
            // as a clean 409 — see GlobalExceptionHandler) instead of silently
            // orphaning seat/booking data. That's intentional: an aircraft
            // that's already been booked needs a deliberate seat migration,
            // not an automatic one.
            seatRepository.deleteAll(seatRepository.findByAircraft_Id(updatedAircraft.getId()));
            seatRepository.saveAll(buildSeatLayout(updatedAircraft));
        }

        return AircraftMapper.toResponse(updatedAircraft);
    }

    @Override
    public void deleteAircraft(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id : " + id));
        aircraftRepository.delete(aircraft);
    }

    private List<Seat> buildSeatLayout(Aircraft aircraft) {
        int totalSeats = aircraft.getTotalSeats();
        List<Seat> seats = new ArrayList<>();

        int row = 1;
        int generated = 0;

        while (generated < totalSeats) {
            for (char letter : SEAT_LETTERS) {
                if (generated >= totalSeats) break;

                Seat seat = Seat.builder()
                        .seatNumber(row + String.valueOf(letter))
                        .seatClass(resolveClass(row))
                        .seatType(resolveType(letter))
                        .aircraft(aircraft)
                        .build();

                seats.add(seat);
                generated++;
            }
            row++;
        }

        return seats;
    }

    private SeatClass resolveClass(int row) {
        if (row <= 2) return SeatClass.FIRST;
        if (row <= 5) return SeatClass.BUSINESS;
        if (row <= 10) return SeatClass.PREMIUM_ECONOMY;
        return SeatClass.ECONOMY;
    }

    private SeatType resolveType(char letter) {
        return switch (letter) {
            case 'A', 'F' -> SeatType.WINDOW;
            case 'C', 'D' -> SeatType.AISLE;
            default -> SeatType.MIDDLE;
        };
    }
}