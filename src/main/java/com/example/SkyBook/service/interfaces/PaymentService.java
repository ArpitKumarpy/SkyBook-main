package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.BatchPaymentRequestDto;
import com.example.SkyBook.dto.request.PaymentRequestDto;
import com.example.SkyBook.dto.response.PaymentResponseDto;

import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto request, String requesterEmail);
    List<PaymentResponseDto> createPayments(BatchPaymentRequestDto request, String requesterEmail);
    List<PaymentResponseDto> getPaymentsForUser(String email);
    PaymentResponseDto getPaymentById(Long id);
    List<PaymentResponseDto> getAllPayments();
    PaymentResponseDto getPaymentByBooking(Long bookingId);
    PaymentResponseDto updatePayment(Long id, PaymentRequestDto request);
    void deletePayment(Long id);
}