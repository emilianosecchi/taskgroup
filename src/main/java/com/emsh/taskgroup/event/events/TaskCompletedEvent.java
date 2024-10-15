package com.emsh.taskgroup.event.events;

import com.emsh.taskgroup.model.Task;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TaskCompletedEvent extends ApplicationEvent {

    private Task task;

    public TaskCompletedEvent(Object source, Task task) {
        super(source);
        this.task = task;
    }
}