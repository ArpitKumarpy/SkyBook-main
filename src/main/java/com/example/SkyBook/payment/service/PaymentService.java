package com.example.SkyBook.payment.service;

import com.example.SkyBook.payment.dto.PaymentRequestDto;
import com.example.SkyBook.payment.dto.PaymentResponseDto;
import com.example.SkyBook.payment.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {
    private final Map<Long, PaymentResponseDto> payments = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {
        PaymentResponseDto response = new PaymentResponseDto();
        response.setPaymentId(idGenerator.getAndIncrement());
        response.setBookingId(request.getBookingId());
        response.setTransactionId("FAKE_TXN_" + response.getPaymentId());
        response.setStatus(PaymentStatus.PENDING);
        response.setGatewayResponse("INITIATED");
        payments.put(response.getPaymentId(), response);
        return response;
    }

    public PaymentResponseDto confirmPayment(Long paymentId, String gatewayResult) {
        PaymentResponseDto payment = payments.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        if ("SUCCESS".equalsIgnoreCase(gatewayResult)) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.setGatewayResponse("SUCCESS");
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse("FAILED");
        }

        return payment;
    }

    public PaymentResponseDto refundPayment(Long paymentId) {
        PaymentResponseDto payment = payments.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setGatewayResponse("REFUNDED");
        return payment;
    }

    public PaymentResponseDto getPayment(Long paymentId) {
        return payments.get(paymentId);
    }
}
