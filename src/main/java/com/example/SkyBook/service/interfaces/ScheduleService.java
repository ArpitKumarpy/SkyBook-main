package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.ScheduleRequestDto;
import com.example.SkyBook.dto.response.ScheduleResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

    ScheduleResponseDto createSchedule(ScheduleRequestDto request);
    List<ScheduleResponseDto> getAllSchedules();
    ScheduleResponseDto getScheduleById(Long id);
    ScheduleResponseDto updateSchedule(Long id, ScheduleRequestDto request);
    void deleteSchedule(Long id);
    List<ScheduleResponseDto> getSchedulesByFlight(Long flightId);
    List<ScheduleResponseDto> getSchedulesByDate(LocalDate departureDate);

}