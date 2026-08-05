package com.example.email.Controller;

import com.example.email.Domain.EmailModel;
import com.example.email.Dto.EmailDto;
import com.example.email.Service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<EmailModel>> findRecentEmails(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(emailService.findRecentEmails(limit));
    }

    @GetMapping("/{emailId}")
    public ResponseEntity<EmailModel> findById(@PathVariable UUID emailId) {
        return ResponseEntity.ok(emailService.findById(emailId));
    }

    @PostMapping("/send")
    public ResponseEntity<EmailModel> sendEmail(@RequestBody @Valid EmailDto emailDto) {
        return ResponseEntity.ok(emailService.sendEmail(emailDto));
    }
}
