package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.BookingRequestDto;
import com.example.SkyBook.dto.response.BookingResponseDto;
import com.example.SkyBook.entity.Booking;

public class BookingMapper {

    private BookingMapper() {
    }

    public static Booking fromRequestDto(BookingRequestDto dto) {

        return Booking.builder().build();

    }

    public static BookingResponseDto toResponseDto(Booking booking) {

        return new BookingResponseDto(

                booking.getId(),
                booking.getBookingReference(),
                booking.getGroupBookingReference(),
                booking.getBookingStatus(),
                booking.getBaseFare(),
                booking.getSeatSurcharge(),
                booking.getGstAmount(),
                booking.getTotalAmount(),
                booking.getBookedAt(),

                booking.getUser().getId(),
                booking.getSchedule().getId(),
                booking.getSeat().getId(),

                booking.getCreatedAt(),
                booking.getUpdatedAt()

        );

    }

}