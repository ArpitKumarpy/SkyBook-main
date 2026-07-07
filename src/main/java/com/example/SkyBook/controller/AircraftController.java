package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.AircraftRequestDto;
import com.example.SkyBook.dto.response.AircraftResponseDto;
import com.example.SkyBook.service.interfaces.AircraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aircrafts")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AircraftResponseDto createAircraft(
            @Valid @RequestBody AircraftRequestDto request) {

        return aircraftService.createAircraft(request);
    }

    @GetMapping
    public List<AircraftResponseDto> getAllAircraft() {
        return aircraftService.getAllAircraft();
    }

    @GetMapping("/{id}")
    public AircraftResponseDto getAircraftById(@PathVariable Long id) {
        return aircraftService.getAircraftById(id);
    }

    @PutMapping("/{id}")
    public AircraftResponseDto updateAircraft(
            @PathVariable Long id,
            @Valid @RequestBody AircraftRequestDto request) {

        return aircraftService.updateAircraft(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);
    }
}