package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.BookingRequestDto;
import com.example.SkyBook.dto.response.BookingResponseDto;
import com.example.SkyBook.service.interfaces.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @Valid @RequestBody BookingRequestDto request) {

        return bookingService.createBooking(request);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @PathVariable Long bookingId) {

        return bookingService.getBookingById(bookingId);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{bookingId}/cancel")
    public BookingResponseDto cancelBooking(
            @PathVariable Long bookingId) {

        bookingService.cancelBooking(bookingId);

        return bookingService.getBookingById(bookingId);
    }

    @PatchMapping("/{bookingId}/confirm-payment")
    public BookingResponseDto confirmBookingAfterPayment(
            @PathVariable Long bookingId) {

        return bookingService.confirmBookingAfterPayment(bookingId);
    }

}