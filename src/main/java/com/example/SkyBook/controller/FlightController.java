package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.FlightRequestDto;
import com.example.SkyBook.dto.response.FlightResponseDto;
import com.example.SkyBook.dto.response.FlightSearchResponseDto;
import com.example.SkyBook.service.interfaces.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponseDto createFlight(
            @Valid @RequestBody FlightRequestDto request) {

        return flightService.createFlight(request);
    }

    @GetMapping
    public List<FlightResponseDto> getAllFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/{id}")
    public FlightResponseDto getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }

    @PutMapping("/{id}")
    public FlightResponseDto updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequestDto request) {

        return flightService.updateFlight(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }


    @GetMapping("/search")
    public List<FlightSearchResponseDto> searchFlights(

            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam LocalDate departureDate

        ) {

        return flightService.searchFlights(
                source,
                destination,
                departureDate
        );

    }
}