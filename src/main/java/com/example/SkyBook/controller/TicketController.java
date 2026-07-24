package com.example.SkyBook.controller;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.TicketResponseDto;
import com.example.SkyBook.entity.Ticket;
import com.example.SkyBook.entity.User;
import com.example.SkyBook.enums.Role;
import com.example.SkyBook.exception.AccessDeniedException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.repository.UserRepository;
import com.example.SkyBook.service.interfaces.TicketService;
import com.example.SkyBook.service.pdf.PdfService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class TicketController {

    private final TicketService ticketService;
    private final PdfService pdfService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Ticket createTicket(@Valid @RequestBody TicketRequestDto request) {
        return ticketService.createTicket(request);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public TicketResponseDto getTicketById(@PathVariable Long id, Authentication authentication) {
        return ticketService.getTicketById(id, authentication.getName());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<TicketResponseDto> getAllTickets(Authentication authentication) {
        return ticketService.getAllTickets(authentication.getName());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/number/{ticketNumber}")
    public TicketResponseDto getTicketByNumber(
            @PathVariable String ticketNumber,
            Authentication authentication) {
        return ticketService.getTicketByNumber(ticketNumber, authentication.getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTicket(@PathVariable Long id) {
        ticketService.cancelTicket(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadTicket(
            @PathVariable Long id,
            Authentication authentication) {

        Ticket ticket = ticketService.getTicketEntityById(id);

        User requester = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        boolean isOwner = ticket.getBooking().getUser().getId().equals(requester.getId());
        if (!isOwner && requester.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You do not have access to this ticket.");
        }

        byte[] pdf = pdfService.generateTicketPdf(ticket);
        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + ticket.getTicketNumber() + ".pdf")
                .contentLength(resource.contentLength())
                .body(resource);
    }
}