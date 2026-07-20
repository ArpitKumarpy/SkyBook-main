package com.example.SkyBook.service.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(
            String to,
            String subject,
            String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendTicketEmail(
            String to,
            String subject,
            String body,
            byte[] pdf) {

        try {

            MimeMessage mimeMessage =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mimeMessage,
                            true
                    );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            helper.addAttachment(
                    "SkyBook_Ticket.pdf",
                    new ByteArrayResource(pdf)
            );

            mailSender.send(mimeMessage);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send ticket email.",
                    e
            );
        }
    }
}