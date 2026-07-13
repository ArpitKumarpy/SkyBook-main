package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentReference(String paymentReference);
    Optional<Payment> findByBookingId(Long bookingId);

}