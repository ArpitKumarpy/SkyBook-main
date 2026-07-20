package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.BatchBookingRequestDto;
import com.example.SkyBook.dto.request.BookingRequestDto;
import com.example.SkyBook.dto.request.PassengerRequestDto;
import com.example.SkyBook.dto.response.BookingResponseDto;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Passenger;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.entity.Seat;
import com.example.SkyBook.entity.User;
import com.example.SkyBook.enums.BookingStatus;
import com.example.SkyBook.enums.Role;
import com.example.SkyBook.exception.AccessDeniedException;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.BookingMapper;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.PassengerRepository;
import com.example.SkyBook.repository.ScheduleRepository;
import com.example.SkyBook.repository.SeatRepository;
import com.example.SkyBook.repository.UserRepository;
import com.example.SkyBook.service.interfaces.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepository;
        private final UserRepository userRepository;
        private final ScheduleRepository scheduleRepository;
        private final SeatRepository seatRepository;
        private final PassengerRepository passengerRepository;

        // Customizable without a code change — override these in
        // application.properties (or an environment variable / config
        // server) as skybook.pricing.seat-surcharge.window / .aisle.
        // MIDDLE seats carry no surcharge and aren't configurable here.
        @Value("${skybook.pricing.seat-surcharge.window:350}")
        private BigDecimal windowSurcharge;

        @Value("${skybook.pricing.seat-surcharge.aisle:200}")
        private BigDecimal aisleSurcharge;

        @Override
        @Transactional
        public BookingResponseDto createBooking(BookingRequestDto request, String requesterEmail) {

                User user = userRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found for email : " + requesterEmail));

                Schedule schedule = scheduleRepository.findById(request.scheduleId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Schedule not found with id : " + request.scheduleId()));

                assertScheduleBookable(schedule, 1);

                Seat seat = seatRepository.findById(request.seatId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Seat not found with id : " + request.seatId()));

                String groupReference = generateBookingReference();
                Booking booking = reserveSeat(user, schedule, seat, request.passenger(), groupReference);

                schedule.setAvailableSeats(schedule.getAvailableSeats() - 1);
                scheduleRepository.save(schedule);

                return BookingMapper.toResponseDto(booking);
        }

        @Override
        @Transactional
        public List<BookingResponseDto> createBookings(BatchBookingRequestDto request, String requesterEmail) {

                User user = userRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found for email : " + requesterEmail));

                Schedule schedule = scheduleRepository.findById(request.scheduleId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Schedule not found with id : " + request.scheduleId()));

                List<BatchBookingRequestDto.SeatBookingDto> seatBookings = request.seats();

                List<Long> seatIds = seatBookings.stream()
                                .map(BatchBookingRequestDto.SeatBookingDto::seatId)
                                .toList();

                if (seatIds.stream().distinct().count() != seatIds.size()) {
                        throw new DuplicateResourceException("The same seat was selected twice.");
                }

                assertScheduleBookable(schedule, seatIds.size());

                // One reference shared by every seat booked in this checkout — the
                // "single unified Booking ID" the batch flow maps to. Each seat
                // still gets its own Booking row (own bookingReference) because
                // Ticket/Payment/seat-status all key off an individual booking.
                String groupReference = generateBookingReference();

                List<Booking> bookings = new ArrayList<>();

                for (BatchBookingRequestDto.SeatBookingDto seatBooking : seatBookings) {
                        Seat seat = seatRepository.findById(seatBooking.seatId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Seat not found with id : " + seatBooking.seatId()));

                        bookings.add(reserveSeat(user, schedule, seat, seatBooking.passenger(), groupReference));
                }

                schedule.setAvailableSeats(schedule.getAvailableSeats() - seatIds.size());
                scheduleRepository.save(schedule);

                return bookings.stream().map(BookingMapper::toResponseDto).toList();
        }

        // Shared by single and batch booking. Now also creates the Passenger
        // record for this seat, so the person paying doesn't have to be the
        // person flying — each seat carries its own passenger details.
        private Booking reserveSeat(User user, Schedule schedule, Seat seat, PassengerRequestDto passengerDetails, String groupBookingReference) {

                Long scheduleAircraftId = schedule.getFlight().getAircraft().getId();
                if (!seat.getAircraft().getId().equals(scheduleAircraftId)) {
                        throw new IllegalStateException(
                                        "Seat " + seat.getSeatNumber() + " does not belong to this flight's aircraft.");
                }

                if (bookingRepository.existsBySeatIdAndBookingStatusNot(seat.getId(), BookingStatus.CANCELLED)) {
                        throw new DuplicateResourceException("Seat " + seat.getSeatNumber() + " is already booked.");
                }

                FareBreakdown fare = calculateFareBreakdown(schedule, seat);

                Booking booking = Booking.builder()
                                .user(user)
                                .schedule(schedule)
                                .seat(seat)
                                .bookingStatus(BookingStatus.PENDING)
                                .bookedAt(LocalDateTime.now())
                                .reservationExpiresAt(LocalDateTime.now().plusMinutes(15))
                                .bookingReference(generateBookingReference())
                                .groupBookingReference(groupBookingReference)
                                .baseFare(fare.classFare())
                                .seatSurcharge(fare.seatSurcharge())
                                .gstAmount(fare.gst())
                                .totalAmount(fare.total())
                                .build();

                Booking savedBooking = bookingRepository.save(booking);

                Passenger passenger = Passenger.builder()
                                .firstName(passengerDetails.firstName())
                                .lastName(passengerDetails.lastName())
                                .gender(passengerDetails.gender())
                                .dateOfBirth(passengerDetails.dateOfBirth())
                                .passportNumber(passengerDetails.passportNumber())
                                .nationality(passengerDetails.nationality())
                                .booking(savedBooking)
                                .build();

                passengerRepository.save(passenger);

                return savedBooking;
        }

        private void assertScheduleBookable(Schedule schedule, int seatsRequested) {

                LocalDateTime departureDateTime = LocalDateTime.of(
                                schedule.getDepartureDate(), schedule.getDepartureTime());

                if (departureDateTime.isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("Flight has already departed");
                }

                if (schedule.getAvailableSeats() < seatsRequested) {
                        throw new IllegalStateException("Not enough seats available for this flight");
                }
        }

        @Override
        public List<BookingResponseDto> getAllBookings(String requesterEmail) {

                User requester = userRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                List<Booking> bookings = requester.getRole() == Role.ADMIN
                                ? bookingRepository.findAll()
                                : bookingRepository.findByUser_Id(requester.getId());

                return bookings.stream().map(BookingMapper::toResponseDto).toList();
        }

        @Override
        public BookingResponseDto getBookingById(Long bookingId, String requesterEmail) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found with id : " + bookingId));

                assertOwnerOrAdmin(booking, requesterEmail);

                return BookingMapper.toResponseDto(booking);
        }

        @Override
        public BookingResponseDto cancelBooking(Long bookingId, String requesterEmail) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found with id : " + bookingId));

                assertOwnerOrAdmin(booking, requesterEmail);

                boolean wasHoldingASeat = booking.getBookingStatus() != BookingStatus.CANCELLED;

                booking.setBookingStatus(BookingStatus.CANCELLED);
                Booking updatedBooking = bookingRepository.save(booking);

                if (wasHoldingASeat) {
                        Schedule schedule = booking.getSchedule();
                        schedule.setAvailableSeats(schedule.getAvailableSeats() + 1);
                        scheduleRepository.save(schedule);
                }

                return BookingMapper.toResponseDto(updatedBooking);
        }

        @Override
        public BookingResponseDto confirmBookingAfterPayment(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found with id : " + bookingId));

                if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                        throw new DuplicateResourceException("Booking is already confirmed.");
                }

                booking.setBookingStatus(BookingStatus.CONFIRMED);
                booking.setReservationExpiresAt(null);

                Booking updatedBooking = bookingRepository.save(booking);

                return BookingMapper.toResponseDto(updatedBooking);
        }

        private void assertOwnerOrAdmin(Booking booking, String requesterEmail) {
                User requester = userRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found for email : " + requesterEmail));

                boolean isOwner = booking.getUser().getId().equals(requester.getId());
                boolean isAdmin = requester.getRole() == Role.ADMIN;

                if (!isOwner && !isAdmin) {
                        throw new AccessDeniedException("You do not have access to this booking.");
                }
        }

        // Base fare (by seat class) + seat-preference surcharge (by seat type),
        // then GST on top of the combined amount. Surcharge amounts come from
        // application.properties (see the @Value fields above) so they can be
        // tuned without a redeploy of business logic.
        private FareBreakdown calculateFareBreakdown(Schedule schedule, Seat seat) {
                BigDecimal baseFare = BigDecimal.valueOf(schedule.getFlight().getPrice());
                BigDecimal classFare = switch (seat.getSeatClass()) {
                        case ECONOMY -> baseFare;
                        case PREMIUM_ECONOMY -> baseFare.multiply(BigDecimal.valueOf(1.25));
                        case BUSINESS -> baseFare.multiply(BigDecimal.valueOf(1.75));
                        case FIRST -> baseFare.multiply(BigDecimal.valueOf(2.50));
                };

                BigDecimal seatSurcharge = switch (seat.getSeatType()) {
                        case WINDOW -> windowSurcharge;
                        case AISLE -> aisleSurcharge;
                        case MIDDLE -> BigDecimal.ZERO;
                };

                BigDecimal preTax = classFare.add(seatSurcharge);
                BigDecimal gst = preTax.multiply(BigDecimal.valueOf(0.18));

                return new FareBreakdown(classFare, seatSurcharge, gst, preTax.add(gst));
        }

        // Itemized price-slip data: base class fare, the seat-preference
        // surcharge, and GST, so the checkout UI can show each line rather
        // than just a single total.
        public record FareBreakdown(BigDecimal classFare, BigDecimal seatSurcharge, BigDecimal gst, BigDecimal total) {
        }

        private String generateBookingReference() {
                String reference;
                do {
                        reference = "SB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                } while (bookingRepository.existsByBookingReference(reference));
                return reference;
        }
}