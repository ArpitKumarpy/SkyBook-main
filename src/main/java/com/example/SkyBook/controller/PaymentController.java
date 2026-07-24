package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.BatchPaymentRequestDto;
import com.example.SkyBook.dto.request.PaymentRequestDto;
import com.example.SkyBook.dto.response.PaymentResponseDto;
import com.example.SkyBook.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public PaymentResponseDto createPayment(
            @Valid @RequestBody PaymentRequestDto request,
            Authentication authentication) {

        return paymentService.createPayment(request, authentication.getName());
    }

    // Own payment history — this is what the new "Payment History" page uses.
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/history")
    public List<PaymentResponseDto> getMyPayments(Authentication authentication) {
        return paymentService.getPaymentsForUser(authentication.getName());
    }

    // Admin-only: every payment across every user.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<PaymentResponseDto> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/booking/{bookingId}")
    public PaymentResponseDto getPaymentByBooking(@PathVariable Long bookingId) {
        return paymentService.getPaymentByBooking(bookingId);
    }

    // update/delete on payments is a strange admin action (payments should
    // generally be immutable records) — leaving these here but admin-gated
    // in case you use them for refund corrections.
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public PaymentResponseDto updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDto request) {
        return paymentService.updatePayment(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/batch")
    public List<PaymentResponseDto> createPayments(
            @Valid @RequestBody BatchPaymentRequestDto request,
            Authentication authentication) {
        return paymentService.createPayments(request, authentication.getName());
    }
}