package com.example.SkyBook.scheduler;

import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.enums.BookingStatus;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingReservationScheduler {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredReservations() {

        System.out.println("Scheduler is running...");
        List<Booking> pendingBookings =
                bookingRepository.findByBookingStatus(
                        BookingStatus.PENDING);

        for (Booking booking : pendingBookings) {

            if (booking.getReservationExpiresAt() != null
                    && booking.getReservationExpiresAt().isBefore(LocalDateTime.now())) {

                booking.setBookingStatus(
                        BookingStatus.CANCELLED);

                booking.setReservationExpiresAt(null);

                bookingRepository.save(booking);

                Schedule schedule = booking.getSchedule();

                schedule.setAvailableSeats(
                        schedule.getAvailableSeats() + 1);

                scheduleRepository.save(schedule);

                log.info(
                        "Reservation expired for booking {}",
                        booking.getBookingReference());
            }
        }
    }
}