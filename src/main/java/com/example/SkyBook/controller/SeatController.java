package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.SeatRequestDto;
import com.example.SkyBook.dto.response.SeatAvailabilityResponseDto;
import com.example.SkyBook.dto.response.SeatResponseDto;
import com.example.SkyBook.service.interfaces.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class SeatController {

    private final SeatService seatService;

    // Admin manual seat creation is still here for corrections, but the
    // frontend no longer offers a "create seat" form by default since
    // seats are generated automatically per-aircraft.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeatResponseDto createSeat(@Valid @RequestBody SeatRequestDto request) {
        return seatService.createSeat(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<SeatResponseDto> getAllSeats() {
        return seatService.getAllSeats();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public SeatResponseDto getSeatById(@PathVariable Long id) {
        return seatService.getSeatById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public SeatResponseDto updateSeat(@PathVariable Long id, @Valid @RequestBody SeatRequestDto request) {
        return seatService.updateSeat(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/aircraft/{aircraftId}")
    public List<SeatResponseDto> getSeatsByAircraft(@PathVariable Long aircraftId) {
        return seatService.getSeatsByAircraft(aircraftId);
    }

    // Public to authenticated users — powers the visual seat picker on the
    // user booking flow. Returns every seat on the aircraft with its
    // AVAILABLE/BOOKED status for this specific schedule.
    @GetMapping("/availability/{scheduleId}")
    public List<SeatAvailabilityResponseDto> getSeatAvailability(@PathVariable Long scheduleId) {
        return seatService.getSeatAvailability(scheduleId);
    }
}