package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.PassengerRequestDto;
import com.example.SkyBook.dto.response.PassengerResponseDto;
import com.example.SkyBook.service.interfaces.PassengerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public PassengerResponseDto createPassenger(
            @Valid @RequestBody PassengerRequestDto request) {

        return passengerService.createPassenger(request);

    }

    @GetMapping("/{id}")
    public PassengerResponseDto getPassengerById(
            @PathVariable Long id) {

        return passengerService.getPassengerById(id);

    }

    @GetMapping
    public List<PassengerResponseDto> getAllPassengers() {

        return passengerService.getAllPassengers();

    }

    @GetMapping("/booking/{bookingId}")
    public List<PassengerResponseDto> getPassengersByBooking(
            @PathVariable Long bookingId) {

        return passengerService.getPassengersByBooking(bookingId);

    }

    @PutMapping("/{id}")
    public PassengerResponseDto updatePassenger(    
        @PathVariable Long id,
            @Valid @RequestBody PassengerRequestDto request) {

        return passengerService.updatePassenger(id, request);

    }

    @DeleteMapping("/{id}")
    public void deletePassenger(
            @PathVariable Long id) {

        passengerService.deletePassenger(id);

    }

}