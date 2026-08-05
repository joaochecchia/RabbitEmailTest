package com.example.email.Service;

import com.example.email.Domain.EmailModel;
import com.example.email.Dto.EmailDto;
import com.example.email.Enums.EmailStatus;
import com.example.email.Repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    private final String emailFrom;

    public EmailService(JavaMailSender javaMailSender,
                        EmailRepository emailRepository,
                        @Value("${spring.mail.from:${spring.mail.username:}}") String emailFrom) {
        this.javaMailSender = javaMailSender;
        this.emailRepository = emailRepository;
        this.emailFrom = emailFrom;
    }

    public EmailModel sendEmail(EmailDto emailDto) {
        EmailModel emailModel = buildEmailModel(emailDto);
        emailModel = emailRepository.save(emailModel);

        try {
            validateEmail(emailModel);
            sendSmtpEmail(emailModel);
            emailModel.setEmailStatus(EmailStatus.SENT);
        } catch (IllegalArgumentException exception) {
            LOGGER.error("Failed to send email to {}: {}", emailModel.getEmailTo(), exception.getMessage());
            emailModel.setEmailStatus(EmailStatus.FAILED);
        } catch (MailException exception) {
            LOGGER.error("Failed to send email to {}", emailModel.getEmailTo(), exception);
            emailModel.setEmailStatus(EmailStatus.FAILED);
        }

        return emailRepository.save(emailModel);
    }

    public List<EmailModel> findRecentEmails(int limit) {
        int normalizedLimit = Math.max(1, Math.min(limit, 100));
        return emailRepository.findAll(
                PageRequest.of(0, normalizedLimit, Sort.by(Sort.Direction.DESC, "sendDateEmail"))
        ).getContent();
    }

    public EmailModel findById(UUID emailId) {
        return emailRepository.findById(emailId).orElseThrow();
    }

    private EmailModel buildEmailModel(EmailDto emailDto) {
        EmailModel emailModel = new EmailModel();
        emailModel.setUserId(emailDto.userId());
        emailModel.setEmailFrom(emailFrom);
        emailModel.setEmailTo(emailDto.emailTo());
        emailModel.setEmailSubject(emailDto.emailSubject());
        emailModel.setEmailBody(emailDto.emailBody());
        emailModel.setSendDateEmail(LocalDateTime.now());
        emailModel.setEmailStatus(EmailStatus.PENDING);
        return emailModel;
    }

    private void sendSmtpEmail(EmailModel emailModel) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailModel.getEmailFrom());
        message.setTo(emailModel.getEmailTo());
        message.setSubject(emailModel.getEmailSubject());
        message.setText(emailModel.getEmailBody());

        javaMailSender.send(message);
    }

    private void validateEmail(EmailModel emailModel) {
        if (!StringUtils.hasText(emailModel.getEmailFrom())) {
            throw new IllegalArgumentException("Configure SMTP_USERNAME, SMTP_FROM, or spring.mail.from before sending emails");
        }

        if (!StringUtils.hasText(emailModel.getEmailTo())) {
            throw new IllegalArgumentException("Email recipient is required. Check if the producer sent emailTo");
        }
    }
}
