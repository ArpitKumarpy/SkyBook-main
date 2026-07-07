package com.example.SkyBook.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.TicketResponseDto;
import com.example.SkyBook.service.interfaces.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponseDto createTicket(@RequestBody TicketRequestDto request) {

        return ticketService.createTicket(request);

    }

    @GetMapping("/{id}")
    public TicketResponseDto getTicketById(@PathVariable Long id) {

        return ticketService.getTicketById(id);

    }

    @GetMapping
    public List<TicketResponseDto> getAllTickets() {

        return ticketService.getAllTickets();

    }

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