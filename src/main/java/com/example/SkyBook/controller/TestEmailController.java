package com.example.SkyBook.controller;

import com.example.SkyBook.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;

    @GetMapping("/test-email")
    public String testEmail() {

        byte[] pdf =
                "This is a test PDF attachment."
                        .getBytes();

        emailService.sendTicketEmail(

                "mitalishandilya2004@gmail.com",

                "SkyBook Ticket Test",

                """
                Congratulations!

                Your PDF attachment is working.
                """,

                pdf
        );

        return "Email Sent Successfully!";
    }
}