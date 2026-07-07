package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.ScheduleRequestDto;
import com.example.SkyBook.dto.response.ScheduleResponseDto;
import com.example.SkyBook.service.interfaces.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponseDto createSchedule(
            @Valid @RequestBody ScheduleRequestDto request) {

        return scheduleService.createSchedule(request);
    }

    @GetMapping
    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ScheduleResponseDto getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id);
    }

    @PutMapping("/{id}")
    public ScheduleResponseDto updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDto request) {

        return scheduleService.updateSchedule(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
    }

    @GetMapping("/flight/{flightId}")
    public List<ScheduleResponseDto> getSchedulesByFlight(
            @PathVariable Long flightId) {

        return scheduleService.getSchedulesByFlight(flightId);
    }

    @GetMapping("/date")
    public List<ScheduleResponseDto> getSchedulesByDate(
            @RequestParam LocalDate departureDate) {

        return scheduleService.getSchedulesByDate(departureDate);
    }
}