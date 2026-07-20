package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.BatchBookingRequestDto;
import com.example.SkyBook.dto.request.BookingRequestDto;
import com.example.SkyBook.dto.response.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto request, String requesterEmail);
    List<BookingResponseDto> createBookings(BatchBookingRequestDto request, String requesterEmail);
    List<BookingResponseDto> getAllBookings(String requesterEmail);
    BookingResponseDto getBookingById(Long bookingId, String requesterEmail);
    BookingResponseDto cancelBooking(Long bookingId, String requesterEmail);
    BookingResponseDto confirmBookingAfterPayment(Long bookingId);
}