package com.tms.service.impl;

import com.tms.model.Task;
import com.tms.model.User;
import com.tms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;

    @Override
    public void sendTaskAssignedEmail(User user, Task task) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("New task assigned: " + task.getName());
        message.setText("You have been assigned a task: '" + task.getName()
                + "' with a deadline " + task.getDueDate());
        mailSender.send(message);
    }

    @Override
    public void sendCommentNotification(User user, Task task, String commentText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("New comment to the task: " + task.getName());
        message.setText("Comment: " + commentText);
        mailSender.send(message);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDeadlineReminder(Task task) {
        User assignee = task.getAssignee();
        if (assignee != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(assignee.getEmail());
            message.setSubject("Reminder: Task deadline is coming soon");
            message.setText(
                    "The deadline for completing the task is tomorrow: '" + task.getName() + "'."
            );
            mailSender.send(message);
        }
    }
}
