package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.SeatRequestDto;
import com.example.SkyBook.dto.response.SeatAvailabilityResponseDto;
import com.example.SkyBook.dto.response.SeatResponseDto;
import com.example.SkyBook.entity.Aircraft;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.entity.Seat;
import com.example.SkyBook.enums.BookingStatus;
import com.example.SkyBook.enums.SeatAvailabilityStatus;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.SeatMapper;
import com.example.SkyBook.repository.AircraftRepository;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.ScheduleRepository;
import com.example.SkyBook.repository.SeatRepository;
import com.example.SkyBook.service.interfaces.SeatService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

        private final SeatRepository seatRepository;
        private final AircraftRepository aircraftRepository;
        private final ScheduleRepository scheduleRepository;
        private final BookingRepository bookingRepository;

        @Override
        public SeatResponseDto createSeat(SeatRequestDto request) {

                if (seatRepository.existsBySeatNumberAndAircraftId(
                                request.seatNumber(),
                                request.aircraftId())) {

                        throw new DuplicateResourceException("Seat already exists for this aircraft.");
                }

                Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Aircraft not found with id : " + request.aircraftId()));

                Seat seat = SeatMapper.fromRequestDto(request);

                seat.setAircraft(aircraft);

                Seat savedSeat = seatRepository.save(seat);

                return SeatMapper.toResponseDto(savedSeat);
        }

        @Override
        public List<SeatResponseDto> getAllSeats() {

                return seatRepository.findAll()
                                .stream()
                                .map(SeatMapper::toResponseDto)
                                .toList();
        }

        @Override
        public SeatResponseDto getSeatById(Long id) {

                Seat seat = seatRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Seat not found with id : " + id));

                return SeatMapper.toResponseDto(seat);
        }

        @Override
        public SeatResponseDto updateSeat(Long id, SeatRequestDto request) {

                Seat seat = seatRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Seat not found with id : " + id));

                Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Aircraft not found with id : " + request.aircraftId()));

                seat.setSeatNumber(request.seatNumber());
                seat.setSeatClass(request.seatClass());
                seat.setSeatType(request.seatType());
                seat.setAircraft(aircraft);

                Seat updatedSeat = seatRepository.save(seat);

                return SeatMapper.toResponseDto(updatedSeat);
        }

        @Override
        public void deleteSeat(Long id) {

                Seat seat = seatRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Seat not found with id : " + id));

                seatRepository.delete(seat);
        }

        @Override
        public List<SeatResponseDto> getSeatsByAircraft(Long aircraftId) {

                return seatRepository.findByAircraft_Id(aircraftId)
                                .stream()
                                .map(SeatMapper::toResponseDto)
                                .toList();
        }

        @Override
        public List<SeatAvailabilityResponseDto> getSeatAvailability(
                        Long scheduleId) {

                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

                Long aircraftId = schedule.getFlight()
                                .getAircraft()
                                .getId();

                List<Seat> seats = seatRepository.findByAircraft_Id(aircraftId);

                List<Booking> bookings = bookingRepository.findByScheduleId(scheduleId);

                Set<Long> bookedSeatIds = bookings.stream()
                                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                                .map(booking -> booking.getSeat().getId())
                                .collect(Collectors.toSet());

                return seats.stream()
                                .map(seat -> new SeatAvailabilityResponseDto(

                                                seat.getId(),
                                                seat.getSeatNumber(),
                                                seat.getSeatClass(),
                                                seat.getSeatType(),

                                                bookedSeatIds.contains(seat.getId())
                                                                ? SeatAvailabilityStatus.BOOKED
                                                                : SeatAvailabilityStatus.AVAILABLE

                                ))
                                .toList();
        }
}