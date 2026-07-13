package com.example.SkyBook.service.interfaces;

import java.util.List;

import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.TicketResponseDto;
import com.example.SkyBook.entity.Ticket;

public interface TicketService {

    Ticket createTicket(TicketRequestDto request);
    TicketResponseDto getTicketById(Long id);
    List<TicketResponseDto> getAllTickets();
    TicketResponseDto getTicketByNumber(String ticketNumber);
    void cancelTicket(Long id);
    Ticket getTicketEntityByBookingId(Long bookingId);
}
