package com.example.email.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record EmailDto(
        UUID userId,
        @NotBlank @Email
        String emailTo,
        @NotBlank
        String emailSubject,
        @NotBlank
        String emailBody
) { }
