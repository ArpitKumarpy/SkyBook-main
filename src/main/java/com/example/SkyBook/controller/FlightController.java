package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.FlightRequestDto;
import com.example.SkyBook.dto.response.FlightResponseDto;
import com.example.SkyBook.dto.response.FlightSearchResponseDto;
import com.example.SkyBook.enums.FlightStatus;
import com.example.SkyBook.service.interfaces.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class FlightController {

    private final FlightService flightService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponseDto createFlight(
            @Valid @RequestBody FlightRequestDto request) {

        return flightService.createFlight(request);
    }

    @GetMapping
    public Page<FlightResponseDto> getAllFlights(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "5") int size,

            @RequestParam(defaultValue = "id") String sortBy,

            @RequestParam(defaultValue = "asc") String direction

    ) {

        return flightService.getAllFlights(

                page,
                size,
                sortBy,
                direction

        );

    }

    @GetMapping("/{id}")
    public FlightResponseDto getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public FlightResponseDto updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequestDto request) {

        return flightService.updateFlight(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }

    @GetMapping("/search")
    public List<FlightSearchResponseDto> searchFlights(

            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam LocalDate departureDate,

            @RequestParam(required = false) String airline,

            @RequestParam(required = false) Double minPrice,

            @RequestParam(required = false) Double maxPrice,

            @RequestParam(required = false) FlightStatus status

    ) {

        return flightService.searchFlights(

                source,
                destination,
                departureDate,

                airline,
                minPrice,
                maxPrice,

                status);
    }
}