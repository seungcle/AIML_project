package com.group.receiptapp.service.email;

import com.group.receiptapp.domain.email.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;


@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendApprovalEmail(EmailMessage emailMessage) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailMessage.getTo());
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getMessage(), true);

            mailSender.send(message);
            log.info("Approval email sent successfully to {}", emailMessage.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send approval email to {}: {}", emailMessage.getTo(), e.getMessage(), e);
        }
    }
}