package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.ScheduleRequestDto;
import com.example.SkyBook.dto.response.ScheduleResponseDto;
import com.example.SkyBook.entity.Flight;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.ScheduleMapper;
import com.example.SkyBook.repository.FlightRepository;
import com.example.SkyBook.repository.ScheduleRepository;
import com.example.SkyBook.service.interfaces.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final FlightRepository flightRepository;

    @Override
    public ScheduleResponseDto createSchedule(ScheduleRequestDto request) {

        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flight not found with id : " + request.flightId()));

        Schedule schedule = ScheduleMapper.fromRequestDto(request);

        schedule.setFlight(flight);
        schedule.setArrivalDate(computeArrivalDate(request.departureDate(), request.departureTime(), request.arrivalTime()));

        // Automatically initialize available seats
        schedule.setAvailableSeats(flight.getAircraft().getTotalSeats());

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return ScheduleMapper.toResponseDto(savedSchedule);
    }

    @Override
    public List<ScheduleResponseDto> getAllSchedules() {

        return scheduleRepository.findAll()
                .stream()
                .map(ScheduleMapper::toResponseDto)
                .toList();
    }

    @Override
    public ScheduleResponseDto getScheduleById(Long id) {

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Schedule not found with id : " + id));

        return ScheduleMapper.toResponseDto(schedule);
    }

    @Override
    public ScheduleResponseDto updateSchedule(Long id, ScheduleRequestDto request) {

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Schedule not found with id : " + id));

        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flight not found with id : " + request.flightId()));

        schedule.setDepartureDate(request.departureDate());
        schedule.setDepartureTime(request.departureTime());
        schedule.setArrivalTime(request.arrivalTime());
        schedule.setArrivalDate(computeArrivalDate(request.departureDate(), request.departureTime(), request.arrivalTime()));
        schedule.setFlight(flight);

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return ScheduleMapper.toResponseDto(updatedSchedule);
    }

    @Override
    public void deleteSchedule(Long id) {

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Schedule not found with id : " + id));

        scheduleRepository.delete(schedule);
    }

    @Override
    public List<ScheduleResponseDto> getSchedulesByFlight(Long flightId) {

        return scheduleRepository.findByFlight_Id(flightId)
                .stream()
                .map(ScheduleMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ScheduleResponseDto> getSchedulesByDate(LocalDate departureDate) {

        return scheduleRepository.findByDepartureDate(departureDate)
                .stream()
                .map(ScheduleMapper::toResponseDto)
                .toList();
    }

    // If the arrival clock time is earlier than the departure clock time,
    // the flight crossed midnight, so arrival is the following day. This
    // only handles a single midnight crossing (fine for any realistic
    // flight duration in this app — nothing here flies more than 24h).
    private LocalDate computeArrivalDate(LocalDate departureDate, LocalTime departureTime, LocalTime arrivalTime) {
        return arrivalTime.isBefore(departureTime) ? departureDate.plusDays(1) : departureDate;
    }
}