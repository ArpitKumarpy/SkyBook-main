package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.enums.BookingStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByBookingReference(String bookingReference);

    boolean existsBySeatId(Long seatId);

    boolean existsBySeatIdAndBookingStatus(
            Long seatId,
            BookingStatus bookingStatus
    );

    boolean existsByScheduleIdAndSeatIdAndBookingStatus(
        Long scheduleId,
        Long seatId,
        BookingStatus bookingStatus
);

    List<Booking> findByScheduleId(Long scheduleId);
    List<Booking> findByBookingStatus(BookingStatus bookingStatus);
}