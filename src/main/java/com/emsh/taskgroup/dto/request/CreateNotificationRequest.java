package com.emsh.taskgroup.dto.request;

import lombok.Data;

@Data
public class CreateNotificationRequest {
    private String message;
    private Long userId;
}
