package com.example.SkyBook.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SkyBook.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);
    boolean existsByTicketNumber(String ticketNumber);
    boolean existsByBooking_Id(Long bookingId);

}