package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.PaymentRequestDto;
import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.PaymentResponseDto;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Payment;
import com.example.SkyBook.entity.Ticket;
import com.example.SkyBook.enums.PaymentStatus;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.PaymentMapper;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.PaymentRepository;
import com.example.SkyBook.service.email.EmailService;
import com.example.SkyBook.service.interfaces.BookingService;
import com.example.SkyBook.service.interfaces.PaymentService;
import com.example.SkyBook.service.interfaces.TicketService;
import com.example.SkyBook.service.pdf.PdfService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

        private final PaymentRepository paymentRepository;
        private final BookingRepository bookingRepository;
        private final BookingService bookingService;
        private final TicketService ticketService;
        private final PdfService pdfService;
        private final EmailService emailService;

        @Transactional
        @Override
        public PaymentResponseDto createPayment(PaymentRequestDto request) {

                Booking booking = bookingRepository.findById(request.bookingId())
                                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));

                if (paymentRepository.findByBookingId(request.bookingId()).isPresent()) {
                        throw new DuplicateResourceException(
                                        "Payment already exists for this booking.");
                }

                Payment payment = PaymentMapper.fromRequestDto(request);

                payment.setBooking(booking);
                payment.setAmount(booking.getTotalAmount());

                payment.setPaymentReference("PAY-" + System.currentTimeMillis());
                payment.setTransactionId("TXN-" + System.currentTimeMillis());

                // For now every payment is successful
                payment.setPaymentStatus(PaymentStatus.SUCCESS);

                payment.setPaymentTime(LocalDateTime.now());

                Payment savedPayment = paymentRepository.save(payment);

                // Since payment is successful, confirm the booking
                bookingService.confirmBookingAfterPayment(
                                booking.getId());

                // Automatically generate ticket
                Ticket ticket = ticketService.createTicket(
                                new TicketRequestDto(
                                                booking.getId()));
                byte[] pdf = pdfService.generateTicketPdf(ticket);

                emailService.sendTicketEmail(

                                booking.getUser().getEmail(),

                                "SkyBook Flight Ticket",

                                """
                                                Dear %s,

                                                Your booking has been confirmed successfully.

                                                Booking Reference : %s

                                                Please find your e-ticket attached.

                                                Thank you for choosing SkyBook.
                                                """
                                                .formatted(

                                                                booking.getUser().getFirstName(),
                                                                booking.getBookingReference()

                                                ),

                                pdf);

                return PaymentMapper.toResponseDto(savedPayment);
        }

        @Override
        public PaymentResponseDto getPaymentById(Long id) {

                Payment payment = paymentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

                return PaymentMapper.toResponseDto(payment);
        }

        @Override
        public List<PaymentResponseDto> getAllPayments() {

                return paymentRepository.findAll()
                                .stream()
                                .map(PaymentMapper::toResponseDto)
                                .toList();
        }

        @Override
        public PaymentResponseDto getPaymentByBooking(Long bookingId) {

                Payment payment = paymentRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

                return PaymentMapper.toResponseDto(payment);
        }

        @Override
        public PaymentResponseDto updatePayment(Long id, PaymentRequestDto request) {

                Payment payment = paymentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

                payment.setPaymentMethod(request.paymentMethod());

                Payment updatedPayment = paymentRepository.save(payment);

                return PaymentMapper.toResponseDto(updatedPayment);
        }

        @Override
        public void deletePayment(Long id) {

                Payment payment = paymentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

                paymentRepository.delete(payment);
        }
}