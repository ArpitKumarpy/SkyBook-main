package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.BatchBookingRequestDto;
import com.example.SkyBook.dto.request.BookingRequestDto;
import com.example.SkyBook.dto.response.BookingResponseDto;
import com.example.SkyBook.service.interfaces.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
            @Valid @RequestBody BookingRequestDto request,
            Authentication authentication) {
        return bookingService.createBooking(request, authentication.getName());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BookingResponseDto> createBookings(
            @Valid @RequestBody BatchBookingRequestDto request,
            Authentication authentication) {
        return bookingService.createBookings(request, authentication.getName());
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(Authentication authentication) {
        return bookingService.getAllBookings(authentication.getName());
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @PathVariable Long bookingId,
            Authentication authentication) {
        return bookingService.getBookingById(bookingId, authentication.getName());
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{bookingId}/cancel")
    public BookingResponseDto cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        return bookingService.cancelBooking(bookingId, authentication.getName());
    }
}