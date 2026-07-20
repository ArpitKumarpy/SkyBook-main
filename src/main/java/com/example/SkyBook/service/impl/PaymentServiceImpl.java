package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.BatchPaymentRequestDto;
import com.example.SkyBook.dto.request.PaymentRequestDto;
import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.PaymentResponseDto;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Payment;
import com.example.SkyBook.entity.Ticket;
import com.example.SkyBook.entity.User;
import com.example.SkyBook.enums.PaymentMethod;
import com.example.SkyBook.enums.PaymentStatus;
import com.example.SkyBook.enums.Role;
import com.example.SkyBook.exception.AccessDeniedException;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.PaymentMapper;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.PaymentRepository;
import com.example.SkyBook.repository.UserRepository;
import com.example.SkyBook.service.email.EmailService;
import com.example.SkyBook.service.interfaces.BookingService;
import com.example.SkyBook.service.interfaces.PaymentService;
import com.example.SkyBook.service.interfaces.TicketService;
import com.example.SkyBook.service.pdf.PdfService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

        private final PaymentRepository paymentRepository;
        private final BookingRepository bookingRepository;
        private final BookingService bookingService;
        private final TicketService ticketService;
        private final PdfService pdfService;
        private final EmailService emailService;
        private final UserRepository userRepository;

        @Override
        @Transactional
        public PaymentResponseDto createPayment(PaymentRequestDto request, String requesterEmail) {

                Booking booking = bookingRepository.findById(request.bookingId())
                                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));

                User requester = userRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                assertOwnerOrAdmin(booking, requester);

                ProcessedPayment processed = processPayment(booking, request.paymentMethod());

                sendTicketEmail(booking, List.of(processed.pdf()), List.of(booking.getBookingReference()));

                return PaymentMapper.toResponseDto(processed.payment());
        }

        @Override
        @Transactional
        public List<PaymentResponseDto> createPayments(BatchPaymentRequestDto request, String requesterEmail) {

                User requester = userRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                List<Booking> bookings = request.bookingIds().stream()
                                .map(id -> bookingRepository.findById(id)
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Booking not found with id : " + id)))
                                .toList();

                bookings.forEach(booking -> assertOwnerOrAdmin(booking, requester));

                List<ProcessedPayment> processedPayments = new ArrayList<>();
                for (Booking booking : bookings) {
                        processedPayments.add(processPayment(booking, request.paymentMethod()));
                }

                // Group by recipient email — normally every booking in a batch belongs
                // to the same user (they came from one seat-selection flow), but an
                // admin could technically pay across bookings for different users, so
                // this sends one merged email per distinct recipient rather than
                // assuming a single owner.
                Map<String, List<ProcessedPayment>> byRecipient = new LinkedHashMap<>();
                for (ProcessedPayment processed : processedPayments) {
                        String email = processed.booking().getUser().getEmail();
                        byRecipient.computeIfAbsent(email, key -> new ArrayList<>()).add(processed);
                }

                for (Map.Entry<String, List<ProcessedPayment>> entry : byRecipient.entrySet()) {
                        List<ProcessedPayment> group = entry.getValue();
                        List<byte[]> pdfs = group.stream().map(ProcessedPayment::pdf).toList();
                        List<String> references = group.stream()
                                        .map(p -> p.booking().getBookingReference())
                                        .toList();

                        sendTicketEmail(group.get(0).booking(), pdfs, references);
                }

                return processedPayments.stream()
                                .map(p -> PaymentMapper.toResponseDto(p.payment()))
                                .toList();
        }

        // Creates the Payment, confirms the Booking, generates the Ticket + its
        // individual PDF — but does NOT send an email. Email dispatch is handled
        // by the caller so batch payments can merge multiple PDFs into one email
        // instead of firing one email per seat.
        private ProcessedPayment processPayment(Booking booking, PaymentMethod paymentMethod) {

                if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
                        throw new DuplicateResourceException(
                                        "Payment already exists for booking " + booking.getId() + ".");
                }

                Payment payment = new Payment();
                payment.setBooking(booking);
                payment.setPaymentMethod(paymentMethod);
                payment.setAmount(booking.getTotalAmount());
                payment.setPaymentReference("PAY-" + System.currentTimeMillis());
                payment.setTransactionId("TXN-" + System.currentTimeMillis());
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setPaymentTime(LocalDateTime.now());

                Payment savedPayment = paymentRepository.save(payment);

                bookingService.confirmBookingAfterPayment(booking.getId());

                Ticket ticket = ticketService.createTicket(new TicketRequestDto(booking.getId()));
                byte[] pdf = pdfService.generateTicketPdf(ticket);

                return new ProcessedPayment(savedPayment, ticket, booking, pdf);
        }

        private void sendTicketEmail(Booking anyBookingInGroup, List<byte[]> pdfs, List<String> bookingReferences) {

                byte[] attachment = mergePdfs(pdfs);

                String referenceList = String.join(", ", bookingReferences);
                String seatWord = bookingReferences.size() == 1 ? "booking" : "bookings";

                emailService.sendTicketEmail(
                                anyBookingInGroup.getUser().getEmail(),
                                "SkyBook Flight Ticket" + (bookingReferences.size() > 1 ? "s" : ""),
                                """
                                                Dear %s,

                                                Your %s has been confirmed successfully.

                                                Booking Reference(s) : %s

                                                Please find your e-ticket(s) attached — %d seat%s in this confirmation.

                                                Thank you for choosing SkyBook.
                                                """.formatted(
                                                anyBookingInGroup.getUser().getFirstName(),
                                                seatWord,
                                                referenceList,
                                                bookingReferences.size(),
                                                bookingReferences.size() == 1 ? "" : "s"
                                ),
                                attachment);
        }

        // Merges N individual single-seat PDFs into one multi-page document.
        // No-op (returns the original bytes) when there's only one PDF, so the
        // single-payment path avoids the merge overhead entirely.
        private byte[] mergePdfs(List<byte[]> pdfs) {
                if (pdfs.size() == 1) {
                        return pdfs.get(0);
                }

                try {
                        PDFMergerUtility merger = new PDFMergerUtility();
                        ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream();
                        merger.setDestinationStream(mergedOutput);

                        for (byte[] pdf : pdfs) {
                                merger.addSource(new ByteArrayInputStream(pdf));
                        }

                        merger.mergeDocuments(null);
                        return mergedOutput.toByteArray();

                } catch (IOException e) {
                        throw new IllegalStateException("Failed to merge ticket PDFs.", e);
                }
        }

        private void assertOwnerOrAdmin(Booking booking, User requester) {
                boolean isOwner = booking.getUser().getId().equals(requester.getId());
                if (!isOwner && requester.getRole() != Role.ADMIN) {
                        throw new AccessDeniedException("You cannot pay for someone else's booking.");
                }
        }

        // Small internal carrier so processPayment can hand back everything the
        // email step needs without a separate lookup.
        private record ProcessedPayment(Payment payment, Ticket ticket, Booking booking, byte[] pdf) {
        }

        @Override
        public List<PaymentResponseDto> getPaymentsForUser(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
                return paymentRepository.findByBooking_User_Id(user.getId())
                                .stream().map(PaymentMapper::toResponseDto).toList();
        }

        @Override
        public PaymentResponseDto getPaymentById(Long id) {
                Payment payment = paymentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
                return PaymentMapper.toResponseDto(payment);
        }

        @Override
        public List<PaymentResponseDto> getAllPayments() {
                return paymentRepository.findAll().stream().map(PaymentMapper::toResponseDto).toList();
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