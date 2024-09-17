package com.emsh.taskgroup.service;

import com.emsh.taskgroup.model.Notification;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(User user, String message) {
        var notification = Notification.builder()
                .isRead(false)
                .message(message)
                .user(user)
                .creationTimestamp(LocalDateTime.now())
                .build();
        return this.notificationRepository.save(notification);
    }

}
