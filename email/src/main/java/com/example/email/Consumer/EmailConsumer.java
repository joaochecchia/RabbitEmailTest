package com.example.email.Consumer;

import com.example.email.Dto.EmailDto;
import com.example.email.Config.RabbitMq;
import com.example.email.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMq.EMAIL_QUEUE)
    public void listenEmail(@Payload EmailDto emailDto){
        LOGGER.info("Received email event for user {} to {}", emailDto.userId(), emailDto.emailTo());
        emailService.sendEmail(emailDto);
    }
}
