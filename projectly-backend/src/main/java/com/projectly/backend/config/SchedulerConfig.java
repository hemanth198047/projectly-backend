package com.projectly.backend.config;

import com.projectly.backend.service.ReminderService;
import com.projectly.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final TaskService taskService;
    private final ReminderService reminderService;

    @Scheduled(fixedRate = 3600000)
    public void processRecurringTasks() {
        taskService.processRecurringTasks();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyReminder() {
        reminderService.sendDailyReminder();
    }
}
