package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.FlightRequestDto;
import com.example.SkyBook.dto.response.FlightResponseDto;
import com.example.SkyBook.dto.response.FlightSearchResponseDto;
import com.example.SkyBook.entity.Aircraft;
import com.example.SkyBook.entity.Flight;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.FlightMapper;
import com.example.SkyBook.repository.AircraftRepository;
import com.example.SkyBook.repository.FlightRepository;
import com.example.SkyBook.repository.ScheduleRepository;
import com.example.SkyBook.service.interfaces.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public FlightResponseDto createFlight(FlightRequestDto request) {

        if (flightRepository.existsByFlightNumber(request.flightNumber())) {
            throw new DuplicateResourceException("Flight number already exists.");
        }

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aircraft not found with id : " + request.aircraftId()));

        Flight flight = FlightMapper.toEntity(request);

        flight.setAircraft(aircraft);

        Flight savedFlight = flightRepository.save(flight);

        return FlightMapper.toResponse(savedFlight);
    }

    @Override
    public List<FlightResponseDto> getAllFlights() {

        return flightRepository.findAll()
                .stream()
                .map(FlightMapper::toResponse)
                .toList();
    }

    @Override
    public FlightResponseDto getFlightById(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Flight not found with id : " + id));

        return FlightMapper.toResponse(flight);
    }

    @Override
    public FlightResponseDto updateFlight(Long id, FlightRequestDto request) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Flight not found with id : " + id));

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aircraft not found with id : " + request.aircraftId()));

        flight.setFlightNumber(request.flightNumber());
        flight.setAirlineName(request.airlineName());
        flight.setSource(request.source());
        flight.setDestination(request.destination());
        flight.setPrice(request.price());
        flight.setStatus(request.status());
        flight.setAircraft(aircraft);

        Flight updatedFlight = flightRepository.save(flight);

        return FlightMapper.toResponse(updatedFlight);
    }

    @Override
    public void deleteFlight(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Flight not found with id : " + id));

        flightRepository.delete(flight);
    }

    @Override
    public List<FlightResponseDto> searchFlights(String source, String destination) {

        return flightRepository.findBySourceAndDestination(source, destination)
                .stream()
                .map(FlightMapper::toResponse)
                .toList();
    }

    @Override
    public List<FlightSearchResponseDto> searchFlights(
        String source,
        String destination,
        LocalDate departureDate) {

    List<Schedule> schedules = scheduleRepository.searchFlights(
            source,
            destination,
            departureDate
    );

    return schedules.stream()
            .map(schedule -> new FlightSearchResponseDto(

                    schedule.getFlight().getId(),
                    schedule.getFlight().getFlightNumber(),
                    schedule.getFlight().getAirlineName(),
                    schedule.getFlight().getSource(),
                    schedule.getFlight().getDestination(),

                    schedule.getDepartureDate(),
                    schedule.getDepartureTime(),
                    schedule.getArrivalTime(),

                    schedule.getFlight().getPrice(),
                    schedule.getAvailableSeats(),
                    schedule.getFlight().getStatus()

            ))
            .toList();
}
}