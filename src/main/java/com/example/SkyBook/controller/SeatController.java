package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.SeatRequestDto;
import com.example.SkyBook.dto.response.SeatResponseDto;
import com.example.SkyBook.service.interfaces.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeatResponseDto createSeat(
            @Valid @RequestBody SeatRequestDto request) {

        return seatService.createSeat(request);
    }

    @GetMapping
    public List<SeatResponseDto> getAllSeats() {
        return seatService.getAllSeats();
    }

    @GetMapping("/{id}")
    public SeatResponseDto getSeatById(@PathVariable Long id) {
        return seatService.getSeatById(id);
    }

    @PutMapping("/{id}")
    public SeatResponseDto updateSeat(
            @PathVariable Long id,
            @Valid @RequestBody SeatRequestDto request) {

        return seatService.updateSeat(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
    }

    @GetMapping("/aircraft/{aircraftId}")
    public List<SeatResponseDto> getSeatsByAircraft(
            @PathVariable Long aircraftId) {

        return seatService.getSeatsByAircraft(aircraftId);
    }
}