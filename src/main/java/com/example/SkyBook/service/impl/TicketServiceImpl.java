package com.example.SkyBook.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.TicketResponseDto;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Ticket;
import com.example.SkyBook.enums.TicketStatus;
import com.example.SkyBook.mapper.TicketMapper;
import com.example.SkyBook.repository.BookingRepository;
import com.example.SkyBook.repository.TicketRepository;
import com.example.SkyBook.service.interfaces.TicketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    @Override
    public TicketResponseDto createTicket(TicketRequestDto request) {

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (ticketRepository.existsByBooking_Id(request.bookingId())) {
            throw new RuntimeException("Ticket already exists for this booking");
        }

        Ticket ticket = TicketMapper.fromRequestDto(request);

        ticket.setBooking(booking);
        ticket.setTicketNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        ticket.setTicketStatus(TicketStatus.ISSUED);
        ticket.setIssueDate(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);

        return TicketMapper.toResponseDto(savedTicket);
    }

    @Override
    public TicketResponseDto getTicketById(Long id) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return TicketMapper.toResponseDto(ticket);
    }

    @Override
    public List<TicketResponseDto> getAllTickets() {

        return ticketRepository.findAll()
                .stream()
                .map(TicketMapper::toResponseDto)
                .toList();
    }

    @Override
    public TicketResponseDto getTicketByNumber(String ticketNumber) {

        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return TicketMapper.toResponseDto(ticket);
    }

    @Override
    public void cancelTicket(Long id) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setTicketStatus(TicketStatus.CANCELLED);

        ticketRepository.save(ticket);
    }

}