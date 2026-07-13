package com.example.SkyBook.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.TicketResponseDto;
import com.example.SkyBook.entity.Ticket;
import com.example.SkyBook.service.interfaces.TicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket createTicket(@Valid @RequestBody TicketRequestDto request) {

        return ticketService.createTicket(request);

    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public TicketResponseDto getTicketById(@PathVariable Long id) {

        return ticketService.getTicketById(id);

    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<TicketResponseDto> getAllTickets() {

        return ticketService.getAllTickets();

    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/number/{ticketNumber}")
    public TicketResponseDto getTicketByNumber(@PathVariable String ticketNumber) {

        return ticketService.getTicketByNumber(ticketNumber);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTicket(@PathVariable Long id) {

        ticketService.cancelTicket(id);

    }

}