package com.emsh.taskgroup.event.events;

import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MembershipRequestCreatedEvent extends ApplicationEvent {
    private final Group group;
    private final User requester;
    public MembershipRequestCreatedEvent(Object source, Group group, User requester) {
        super(source);
        this.group = group;
        this.requester = requester;
    }
}