package com.example.email.Service;

import com.example.email.Domain.EmailModel;
import com.example.email.Dto.EmailDto;
import com.example.email.Enums.EmailStatus;
import com.example.email.Repository.EmailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailRepository emailRepository;

    @Test
    void sendsEmailWhenSenderIsConfigured() {
        when(emailRepository.save(any(EmailModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        EmailService emailService = new EmailService(javaMailSender, emailRepository, "sender@example.com");

        EmailModel result = emailService.sendEmail(new EmailDto(
                UUID.randomUUID(),
                "recipient@example.com",
                "Welcome",
                "Hello"
        ));

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getFrom()).isEqualTo("sender@example.com");
        assertThat(message.getTo()).containsExactly("recipient@example.com");
        assertThat(result.getEmailStatus()).isEqualTo(EmailStatus.SENT);
        verify(emailRepository, times(2)).save(any(EmailModel.class));
    }

    @Test
    void marksEmailAsFailedWhenSenderIsMissing() {
        when(emailRepository.save(any(EmailModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        EmailService emailService = new EmailService(javaMailSender, emailRepository, " ");

        EmailModel result = emailService.sendEmail(new EmailDto(
                UUID.randomUUID(),
                "recipient@example.com",
                "Welcome",
                "Hello"
        ));

        assertThat(result.getEmailStatus()).isEqualTo(EmailStatus.FAILED);
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(2)).save(any(EmailModel.class));
    }
}
