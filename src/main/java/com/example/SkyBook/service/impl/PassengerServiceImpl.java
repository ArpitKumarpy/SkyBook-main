package com.example.SkyBook.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SkyBook.dto.request.PassengerRequestDto;
import com.example.SkyBook.dto.response.PassengerResponseDto;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Passenger;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.PassengerMapper;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.PassengerRepository;
import com.example.SkyBook.service.interfaces.PassengerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService{

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
        

    public PassengerResponseDto createPassenger(PassengerRequestDto request) {
        Booking b = new Booking();
        Booking booking = bookingRepository.findById(b.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        Passenger passenger = PassengerMapper.fromRequestDto(request);
        passenger.setBooking(booking);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return PassengerMapper.toResponseDto(savedPassenger);
    }

    public PassengerResponseDto getPassengerById(Long id) {

        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found."));
        return PassengerMapper.toResponseDto(passenger);
    }

    public List<PassengerResponseDto> getAllPassengers() {
        return passengerRepository.findAll()
                .stream()
                .map(PassengerMapper::toResponseDto)
                .toList();
    }

    public List<PassengerResponseDto> getPassengersByBooking(Long bookingId) {
        return passengerRepository.findByBookingId(bookingId)
                .stream()
                .map(PassengerMapper::toResponseDto)
                .toList();
    }

    public PassengerResponseDto updatePassenger(Long id,
            PassengerRequestDto request) {
        Booking b = new Booking();
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found."));
        Booking booking = bookingRepository.findById(b.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        passenger.setFirstName(request.firstName());
        passenger.setLastName(request.lastName());
        passenger.setGender(request.gender());
        passenger.setDateOfBirth(request.dateOfBirth());
        passenger.setPassportNumber(request.passportNumber());
        passenger.setNationality(request.nationality());
        passenger.setBooking(booking);
        Passenger updatedPassenger = passengerRepository.save(passenger);
        return PassengerMapper.toResponseDto(updatedPassenger);
    }

    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found."));
        passengerRepository.delete(passenger);
    }
}
