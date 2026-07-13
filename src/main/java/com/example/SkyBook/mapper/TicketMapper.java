package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.TicketRequestDto;
import com.example.SkyBook.dto.response.TicketResponseDto;
import com.example.SkyBook.entity.Ticket;

public class TicketMapper {

    private TicketMapper() {
    }

    public static Ticket fromRequestDto(TicketRequestDto dto) {

        return Ticket.builder().build();

    }

    public static TicketResponseDto toResponseDto(Ticket ticket) {

        return new TicketResponseDto(

                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getBooking().getId(),
                ticket.getTicketStatus(),
                ticket.getIssueDate(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()

        );

    }

}