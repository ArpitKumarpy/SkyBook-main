package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.PaymentRequestDto;
import com.example.SkyBook.dto.response.PaymentResponseDto;
import com.example.SkyBook.entity.Payment;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static Payment fromRequestDto(PaymentRequestDto dto) {

        return Payment.builder()
                .paymentMethod(dto.paymentMethod())
                .build();

    }

    public static PaymentResponseDto toResponseDto(Payment payment) {

        return new PaymentResponseDto(

                payment.getId(),
                payment.getPaymentReference(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionId(),
                payment.getPaymentTime(),
                payment.getBooking().getId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()

        );

    }

}