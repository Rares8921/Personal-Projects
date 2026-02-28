package com.example.ihas.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender _mailSender) {
        mailSender = _mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:8080/auth/verify_user?token=" + token;
        String subject = "Confirm your email";
        String content = "Click the following link to verify your email: <a href=\"" + link + "\">Verify</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(System.out);
        }
    }
}
