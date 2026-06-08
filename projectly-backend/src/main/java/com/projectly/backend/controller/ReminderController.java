package com.projectly.backend.controller;


import com.projectly.backend.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping("/send-now")
    public Map<String, String> sendNow() {
        reminderService.sendDailyReminder();
        return Map.of("message", "Reminder sent successfully");
    }
}
