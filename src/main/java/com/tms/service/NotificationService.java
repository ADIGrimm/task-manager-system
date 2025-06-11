package com.tms.service;

import com.tms.model.Task;
import com.tms.model.User;

public interface NotificationService {
    void sendTaskAssignedEmail(User user, Task task);

    void sendCommentNotification(User user, Task task, String commentText);

    void sendDeadlineReminder(Task task);
}
