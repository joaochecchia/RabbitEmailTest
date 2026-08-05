package com.example.Auth.Producer;

import com.example.Auth.Config.RabbitMq;
import com.example.Auth.DTO.EmailDto;
import com.example.Auth.Domain.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
public class AuthProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthProducer.class);

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public void sendCreateEvent(UserModel userModel) {
        EmailDto event = new EmailDto(
                userModel.getUserId(),
                userModel.getEmail(),
                "Bem Vindo",
                "Seu codigo de ativação é: 1"
        );

        rabbitTemplate.convertAndSend("", RabbitMq.EMAIL_QUEUE, event);
        LOGGER.info("Published welcome email event to {} for user {} <{}>",
                RabbitMq.EMAIL_QUEUE, userModel.getUserId(), userModel.getEmail());
    }
}
