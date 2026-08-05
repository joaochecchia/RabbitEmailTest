package com.example.Auth.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMq {

    public static final String EMAIL_QUEUE = "email-queue";

    @Bean
    public Queue emailQueue(){
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter mensagerConverter(){
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
