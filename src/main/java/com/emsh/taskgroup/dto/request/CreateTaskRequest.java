package com.emsh.taskgroup.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTaskRequest {
    private String description;
    private Long creatorUserId;
    private String taskPriority;
    private Long groupId;
}
