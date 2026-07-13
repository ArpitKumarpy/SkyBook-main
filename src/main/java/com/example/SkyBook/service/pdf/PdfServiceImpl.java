package com.example.SkyBook.service.pdf;

import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.entity.Ticket;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateTicketPdf(Ticket ticket) {

        try {

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(PageSize.A4);

            PdfWriter.getInstance(document, out);

            document.open();

            Font title =
                    new Font(Font.HELVETICA, 20, Font.BOLD);

            Paragraph heading =
                    new Paragraph(
                            "SKYBOOK E-TICKET",
                            title);

            heading.setAlignment(Element.ALIGN_CENTER);

            document.add(heading);

            document.add(new Paragraph(" "));

            Booking booking =
                    ticket.getBooking();

            Schedule schedule =
                    booking.getSchedule();

            document.add(new Paragraph(
                    "Ticket Number : "
                            + ticket.getTicketNumber()));

            document.add(new Paragraph(
                    "Booking Reference : "
                            + booking.getBookingReference()));

            document.add(new Paragraph(
                    "Passenger : "
                            + booking.getUser().getFirstName()
                            + " "
                            + booking.getUser().getLastName()));

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Flight : "
                            + schedule.getFlight().getFlightNumber()));

            document.add(new Paragraph(
                    "Airline : "
                            + schedule.getFlight().getAirlineName()));

            document.add(new Paragraph(
                    "From : "
                            + schedule.getFlight().getSource()));

            document.add(new Paragraph(
                    "To : "
                            + schedule.getFlight().getDestination()));

            document.add(new Paragraph(
                    "Departure Date : "
                            + schedule.getDepartureDate()));

            document.add(new Paragraph(
                    "Departure Time : "
                            + schedule.getDepartureTime()));

            document.add(new Paragraph(
                    "Seat : "
                            + booking.getSeat().getSeatNumber()));

            document.add(new Paragraph(
                    "Status : "
                            + booking.getBookingStatus()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Thank you for choosing SkyBook."));

            document.close();

            return out.toByteArray();

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate PDF", e);

        }
    }
}