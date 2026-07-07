package com.example.SkyBook.payment.controller;

import com.example.SkyBook.loyalty.service.LoyaltyService;
import com.example.SkyBook.notification.service.NotificationService;
import com.example.SkyBook.payment.dto.PaymentRequestDto;
import com.example.SkyBook.payment.dto.PaymentResponseDto;
import com.example.SkyBook.payment.service.PaymentService;
import com.example.SkyBook.service.interfaces.BookingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final LoyaltyService loyaltyService;
    private final NotificationService notificationService;

    public PaymentController(PaymentService paymentService, BookingService bookingService,
                             LoyaltyService loyaltyService, NotificationService notificationService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.loyaltyService = loyaltyService;
        this.notificationService = notificationService;
    }

    @PostMapping("/initiate")
    public PaymentResponseDto initiate(@RequestBody PaymentRequestDto request) {
        return paymentService.initiatePayment(request);
    }

    @PostMapping("/{paymentId}/confirm")
    public PaymentResponseDto confirm(@PathVariable Long paymentId, @RequestParam String result) {
        PaymentResponseDto paymentResponse = paymentService.confirmPayment(paymentId, result);
        if ("SUCCESS".equalsIgnoreCase(result) && paymentResponse.getBookingId() != null) {
            bookingService.confirmBookingAfterPayment(paymentResponse.getBookingId());
            loyaltyService.earnPoints(1L, 100);
            notificationService.sendNotification(1L, "Booking Confirmed",
                    "Your booking has been confirmed and loyalty points added.");
        }
        return paymentResponse;
    }

    @PostMapping("/{paymentId}/refund")
    public PaymentResponseDto refund(@PathVariable Long paymentId) {
        return paymentService.refundPayment(paymentId);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseDto getPayment(@PathVariable Long paymentId) {
        return paymentService.getPayment(paymentId);
    }
}
