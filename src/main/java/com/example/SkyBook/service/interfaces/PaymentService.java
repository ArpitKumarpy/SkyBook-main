package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.PaymentRequestDto;
import com.example.SkyBook.dto.response.PaymentResponseDto;

import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto request);
    PaymentResponseDto getPaymentById(Long id);
    List<PaymentResponseDto> getAllPayments();
    PaymentResponseDto getPaymentByBooking(Long bookingId);
    PaymentResponseDto updatePayment(Long id, PaymentRequestDto request);
    void deletePayment(Long id);

}