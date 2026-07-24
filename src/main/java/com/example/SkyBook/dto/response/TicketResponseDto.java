package com.example.SkyBook.dto.response;

import java.time.LocalDateTime;

import com.example.SkyBook.enums.TicketStatus;

public record TicketResponseDto(

        Long id,
        String ticketNumber,
        Long bookingId,
        TicketStatus ticketStatus,
        LocalDateTime issueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}