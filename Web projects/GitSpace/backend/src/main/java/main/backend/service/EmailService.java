package main.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async // Executa pe un thread separat
    public void sendResetPasswordEmail(String to, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Reset your Password - GitClone");

            String content = String.format("""
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Password Reset Request</h2>
                    <p>Hello,</p>
                    <p>You have requested to reset your password.</p>
                    <p>Click the link below to set a new password:</p>
                    <p>
                        <a href="%s" style="background-color: #2563eb; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                            Reset Password
                        </a>
                    </p>
                    <p>Or copy this link: %s</p>
                    <p>This link expires in 1 hour.</p>
                    <br>
                    <p>If you did not request this, please ignore this email.</p>
                </div>
                """, resetLink, resetLink);

            helper.setText(content, true); // true = HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // false = not multipart, "UTF-8" pentru caractere speciale
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // false = plain text (nu HTML)

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);

        } catch (MessagingException e) {
            System.err.println("Failed to send simple email: " + e.getMessage());
        }
    }
}