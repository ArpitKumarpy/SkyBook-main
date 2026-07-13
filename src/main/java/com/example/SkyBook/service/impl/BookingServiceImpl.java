package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.BookingRequestDto;
import com.example.SkyBook.dto.response.BookingResponseDto;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.entity.Seat;
import com.example.SkyBook.entity.User;
import com.example.SkyBook.enums.BookingStatus;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.BookingMapper;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.ScheduleRepository;
import com.example.SkyBook.repository.SeatRepository;
import com.example.SkyBook.repository.UserRepository;
import com.example.SkyBook.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepository;
        private final UserRepository userRepository;
        private final ScheduleRepository scheduleRepository;
        private final SeatRepository seatRepository;

        @Override
        public BookingResponseDto createBooking(BookingRequestDto request) {

                User user = userRepository.findById(request.userId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with id : " + request.userId()));

                Schedule schedule = scheduleRepository.findById(request.scheduleId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Schedule not found with id : " + request.scheduleId()));

                Seat seat = seatRepository.findById(request.seatId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Seat not found with id : " + request.seatId()));

                LocalDateTime departureDateTime = LocalDateTime.of(
                                schedule.getDepartureDate(),
                                schedule.getDepartureTime());

                if (departureDateTime.isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("Flight has already departed");
                }

                if (schedule.getAvailableSeats() <= 0) {
                        throw new IllegalStateException("No seats available for this flight");
                }

                if (bookingRepository.existsBySeatIdAndBookingStatus(
                                request.seatId(),
                                BookingStatus.CONFIRMED)) {

                        throw new DuplicateResourceException("Seat is already booked");
                }

                Booking booking = BookingMapper.fromRequestDto(request);

                booking.setUser(user);
                booking.setSchedule(schedule);
                booking.setSeat(seat);

                booking.setBookingStatus(BookingStatus.PENDING);

                booking.setBookedAt(LocalDateTime.now());

                booking.setReservationExpiresAt(
                                LocalDateTime.now().plusMinutes(15));

                booking.setBookingReference(generateBookingReference());

                booking.setTotalAmount(
                                calculateFareWithGST(schedule, seat));

                schedule.setAvailableSeats(
                                schedule.getAvailableSeats() - 1);

                scheduleRepository.save(schedule);

                Booking savedBooking = bookingRepository.save(booking);

                return BookingMapper.toResponseDto(savedBooking);
        }

        @Override
        public List<BookingResponseDto> getAllBookings() {

                return bookingRepository.findAll()
                                .stream()
                                .map(BookingMapper::toResponseDto)
                                .toList();
        }

        @Override
        public BookingResponseDto getBookingById(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found with id : " + bookingId));

                return BookingMapper.toResponseDto(booking);
        }

        @Override
        public BookingResponseDto cancelBooking(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found with id : " + bookingId));

                booking.setBookingStatus(BookingStatus.CANCELLED);

                Booking updatedBooking = bookingRepository.save(booking);

                Schedule schedule = booking.getSchedule();

                schedule.setAvailableSeats(
                                schedule.getAvailableSeats() + 1);

                scheduleRepository.save(schedule);

                return BookingMapper.toResponseDto(updatedBooking);
        }

        private BigDecimal calculateFareWithGST(
                        Schedule schedule,
                        Seat seat) {

                BigDecimal baseFare = BigDecimal.valueOf(
                                schedule.getFlight().getPrice());

                BigDecimal fare = switch (seat.getSeatClass()) {

                        case ECONOMY -> baseFare;

                        case PREMIUM_ECONOMY ->
                                baseFare.multiply(BigDecimal.valueOf(1.25));

                        case BUSINESS ->
                                baseFare.multiply(BigDecimal.valueOf(1.75));

                        case FIRST ->
                                baseFare.multiply(BigDecimal.valueOf(2.50));
                };

                BigDecimal gst = fare.multiply(BigDecimal.valueOf(0.18));

                return fare.add(gst);
        }

        private String generateBookingReference() {

                String reference;

                do {
                        reference = "SB-" +
                                        UUID.randomUUID()
                                                        .toString()
                                                        .substring(0, 8)
                                                        .toUpperCase();

                } while (bookingRepository.existsByBookingReference(reference));

                return reference;
        }

        @Override
        public BookingResponseDto confirmBookingAfterPayment(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found with id : " + bookingId));

                if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                        throw new DuplicateResourceException(
                                        "Booking is already confirmed.");
                }

                Schedule schedule = booking.getSchedule();

                if (schedule.getAvailableSeats() <= 0) {
                        throw new IllegalStateException(
                                        "No seats available.");
                }

                booking.setBookingStatus(BookingStatus.CONFIRMED);

                booking.setReservationExpiresAt(null);
                
                schedule.setAvailableSeats(
                                schedule.getAvailableSeats() - 1);

                scheduleRepository.save(schedule);

                Booking updatedBooking = bookingRepository.save(booking);

                return BookingMapper.toResponseDto(updatedBooking);
        }
}