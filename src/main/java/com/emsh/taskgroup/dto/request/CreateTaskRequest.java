package com.emsh.taskgroup.dto.request;

import lombok.Data;

@Data
public class CreateTaskRequest {
    private String description;
    private Long creatorUserId;
    private String taskPriority;
}
