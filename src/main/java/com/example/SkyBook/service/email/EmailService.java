package com.example.SkyBook.service.email;

public interface EmailService {

    void sendEmail(
            String to,
            String subject,
            String body
    );

    void sendTicketEmail(
            String to,
            String subject,
            String body,
            byte[] pdf
    );

}