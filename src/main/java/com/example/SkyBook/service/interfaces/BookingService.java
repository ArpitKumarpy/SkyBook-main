package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.BookingRequestDto;
import com.example.SkyBook.dto.response.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto request);
    List<BookingResponseDto> getAllBookings();
    BookingResponseDto getBookingById(Long bookingId);
    BookingResponseDto cancelBooking(Long bookingId);
    BookingResponseDto confirmBookingAfterPayment(Long bookingId);

}