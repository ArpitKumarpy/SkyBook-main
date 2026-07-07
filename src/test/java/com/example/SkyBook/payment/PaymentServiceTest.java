package com.example.SkyBook.payment;

import com.example.SkyBook.payment.dto.PaymentRequestDto;
import com.example.SkyBook.payment.dto.PaymentResponseDto;
import com.example.SkyBook.payment.enums.PaymentStatus;
import com.example.SkyBook.payment.service.PaymentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Test
    void shouldCreateAndConfirmPaymentFlow() {
        PaymentService paymentService = new PaymentService();

        PaymentRequestDto request = new PaymentRequestDto();
        request.setBookingId(101L);
        request.setAmount(250.0);
        request.setCurrency("USD");
        request.setUserId(7L);

        PaymentResponseDto response = paymentService.initiatePayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.PENDING, response.getStatus());
        assertNotNull(response.getPaymentId());
        assertNotNull(response.getTransactionId());

        PaymentResponseDto confirmed = paymentService.confirmPayment(response.getPaymentId(), "SUCCESS");
        assertEquals(PaymentStatus.SUCCEEDED, confirmed.getStatus());
        assertEquals("SUCCESS", confirmed.getGatewayResponse());

        PaymentResponseDto refund = paymentService.refundPayment(response.getPaymentId());
        assertEquals(PaymentStatus.REFUNDED, refund.getStatus());
    }
}
