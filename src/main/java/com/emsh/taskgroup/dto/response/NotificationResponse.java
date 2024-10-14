package com.emsh.taskgroup.dto.response;

import com.emsh.taskgroup.model.Notification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long notificationId;
    private Boolean isRead;
    private LocalDateTime creationTimestamp;
    private String message;

    public NotificationResponse(Notification notification) {
        notificationId = notification.getId();
        isRead = notification.getIsRead();
        creationTimestamp = notification.getCreationTimestamp();
        message = notification.getMessage();
    }

}
