package com.projectly.backend.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${app.reminder.email}")
    private String reminderEmail;

    public void sendReminder(String subject, String body) {
        try {
            Resend resend = new Resend(apiKey);
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("ProjectsNGoals <onboarding@resend.dev>")
                    .to(reminderEmail)
                    .subject(subject)
                    .text(body)
                    .build();
            resend.emails().send(params);
            System.out.println("Email sent successfully");
        } catch (ResendException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
