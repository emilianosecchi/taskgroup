package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.response.NotificationResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Notification;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return notificationRepository.save(notification);
    }

    public void markNotificationAsRead(Long notificationId) throws CustomApiException {
        var notification = this.notificationRepository.findById(notificationId);
        if (notification.isEmpty())
            throw new CustomApiException("La notificación no existe", HttpStatus.BAD_REQUEST);
        notification.get().setIsRead(true);
        this.notificationRepository.save(notification.get());
    }

    @Transactional
    public void markAllNotificationsAsRead(Long userId) throws CustomApiException {
        notificationRepository.markAllNotificationsAsRead(userId);
    }

    public List<NotificationResponse> getAllNotificationsForUser(Long userId) {
        Optional<List<Notification>> result = notificationRepository.findByUserIdOrderByCreationTimestampDesc(userId);
        return result.map(notifications -> notifications.stream().map(NotificationResponse::new).toList())
                .orElseGet(ArrayList::new);
    }

    public void deleteNotification(Long notificationId) throws CustomApiException {
        var notification = this.notificationRepository.findById(notificationId);
        if (notification.isEmpty())
            throw new CustomApiException("La notificación no existe", HttpStatus.BAD_REQUEST);
        else
            this.notificationRepository.delete(notification.get());
    }

    @Transactional
    public void deleteAllNotificationsForUser(Long userId) {
        notificationRepository.deleteAllNotifications(userId);
    }

}