package com.example.SkyBook.service.pdf;

import com.example.SkyBook.entity.Ticket;

public interface PdfService {

    byte[] generateTicketPdf(Ticket ticket);

}