package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.PaymentRequestDto;
import com.example.SkyBook.dto.response.PaymentResponseDto;
import com.example.SkyBook.service.interfaces.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public PaymentResponseDto createPayment(
            @Valid @RequestBody PaymentRequestDto request) {

        return paymentService.createPayment(request);
    }

    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentById(
            @PathVariable Long id) {

        return paymentService.getPaymentById(id);
    }

    @GetMapping
    public List<PaymentResponseDto> getAllPayments() {

        return paymentService.getAllPayments();
    }

    @GetMapping("/booking/{bookingId}")
    public PaymentResponseDto getPaymentByBooking(
            @PathVariable Long bookingId) {

        return paymentService.getPaymentByBooking(bookingId);
    }

    
    @PutMapping("/{id}")
    public PaymentResponseDto updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDto request) {

        return paymentService.updatePayment(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(
            @PathVariable Long id) {

        paymentService.deletePayment(id);
    }
}