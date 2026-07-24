package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentReference(String paymentReference);
    Optional<Payment> findByBookingId(Long bookingId);
    List<Payment> findByBooking_User_Id(Long userId);

}